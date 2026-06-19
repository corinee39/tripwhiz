package com.tripwhiz.tripwhizuserback.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripwhiz.tripwhizuserback.common.dto.PageRequestDTO;
import com.tripwhiz.tripwhizuserback.common.dto.PageResponseDTO;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.domain.ThemeCategory;
import com.tripwhiz.tripwhizuserback.product.dto.ProductListDTO;
import com.tripwhiz.tripwhizuserback.product.dto.ProductReadDTO;
import com.tripwhiz.tripwhizuserback.product.repository.ThemeCategoryRepository;
import com.tripwhiz.tripwhizuserback.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@Log4j2
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 목록 조회
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ProductListDTO>> list(
            @RequestParam(required = false) Long tno,
            @RequestParam(required = false) Long cno,
            @RequestParam(required = false) Long scno,
            @Validated PageRequestDTO pageRequestDTO) {
        log.info("상품 목록을 조회합니다. tno: {}, cno: {}, scno: {}", tno, cno, scno);

        PageResponseDTO<ProductListDTO> response = productService.searchProducts(tno, cno, scno, pageRequestDTO);

        log.info("상품 목록 응답: {}", response);
        return ResponseEntity.ok(response);
    }

    // 상품 키워드 검색 및 가격 필터링(JH)
    @GetMapping("/list/search")
    public ResponseEntity<PageResponseDTO<ProductListDTO>> searchWithFilters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Long tno,
            @RequestParam(required = false) Long cno,
            @RequestParam(required = false) Long scno,
            PageRequestDTO pageRequestDTO) {
        log.info("상품 키워드 검색 요청 - keyword: {}, minPrice: {}, maxPrice: {}", keyword, minPrice, maxPrice);

        PageResponseDTO<ProductListDTO> result = productService.searchWithFilters(keyword, minPrice, maxPrice, tno, cno, scno, pageRequestDTO);
        return ResponseEntity.ok(result);
    }


    // 특정 상품 ID로 조회 (이미지 포함)
    @GetMapping("/read/{pno}")
    public ResponseEntity<ProductReadDTO> getProduct(@PathVariable Long pno) {
        log.info("ID로 상품을 조회합니다: {}", pno);

        Optional<ProductReadDTO> productObj = productService.getProductById(pno);

        return productObj.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("해당 ID의 상품을 찾을 수 없습니다: {}", pno);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/add")
    public ResponseEntity<Long> createProduct(
            @RequestPart("productListDTO") String productListDTOJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) throws JsonProcessingException, IOException {
        log.info("===========================");
        log.info("Received productListDTO JSON: {}", productListDTOJson);
        log.info("Received Image Files: {}", imageFiles);

        // JSON -> DTO 변환
        ObjectMapper objectMapper = new ObjectMapper();
        ProductListDTO productListDTO;
        try {
            productListDTO = objectMapper.readValue(productListDTOJson, ProductListDTO.class);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류: {}", e.getMessage());
            throw e;
        }

        log.info("Parsed ProductListDTO: {}", productListDTO);

        if (imageFiles != null) {
            log.info("Received {} image files", imageFiles.size());
        } else {
            log.warn("No image files were provided.");
        }

        // 상품 생성
        Long productId = productService.createProduct(productListDTO, imageFiles);

        return ResponseEntity.ok(productId);
    }

    // 상품 수정
    @PutMapping("/update/{pno}")
    public ResponseEntity<Long> updateProduct(
            @PathVariable Long pno,
            @RequestPart("productListDTO") String productListDTOJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) throws JsonProcessingException, IOException {

        // JSON 문자열을 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        ProductListDTO productListDTO = objectMapper.readValue(productListDTOJson, ProductListDTO.class);

        log.info("Received product update request for PNO {}: {}", pno, productListDTO);

        if (imageFiles != null) {
            log.info("Received {} image files", imageFiles.size());
        }

        // 서비스 호출
        Long updatedProductPno = productService.updateProduct(pno, productListDTO, imageFiles);

        // 수정된 상품 ID 반환
        return ResponseEntity.ok(updatedProductPno);
    }

    // 상품 삭제
    @PutMapping("/delete/{pno}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long pno) {
        log.info("Received product deletion request for PNO {}", pno);
        productService.deleteProduct(pno);
        return ResponseEntity.ok().build();
    }


}

//    // 상품 생성
//    @PostMapping("/add")
//    public ResponseEntity<Long> createProduct(
//            @RequestPart("productListDTO") String productListDTOJson,
//            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
//            @RequestPart("themeCategoryId") Long themeCategoryId
//            ) throws JsonProcessingException, IOException {
//
//        log.info("===========================");
//        log.info(productListDTOJson);
//        log.info(themeCategoryId);
//        log.info(imageFiles);
//
//
//
//        // JSON 문자열을 객체로 변환
//        ObjectMapper objectMapper = new ObjectMapper();
//        ProductListDTO productListDTO = objectMapper.readValue(productListDTOJson, ProductListDTO.class);
//
//        log.info("Received Product: {}", productListDTO);
//
//        if (imageFiles != null) {
//            log.info("Received {} image files", imageFiles.size());
//        }
//
//        // 서비스 호출
//        Long productId = productService.createProduct(productListDTO, imageFiles);
//
//        // 생성된 상품 ID 반환
//        return ResponseEntity.ok(productId);
//    }
//
//    // 상품 수정
//    @PutMapping("/update/{pno}")
//    public ResponseEntity<Long> updateProduct(
//            @PathVariable Long pno,
//            @RequestPart("productListDTO") String productListDTOJson,
//            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) throws JsonProcessingException, IOException {
//
//        // JSON 문자열을 객체로 변환
//        ObjectMapper objectMapper = new ObjectMapper();
//        ProductListDTO productListDTO = objectMapper.readValue(productListDTOJson, ProductListDTO.class);
//
//        log.info("Received product update request for PNO {}: {}", pno, productListDTO);
//
//        if (imageFiles != null) {
//            log.info("Received {} image files", imageFiles.size());
//        }
//
//        // 서비스 호출
//        Long updatedProductPno = productService.updateProduct(pno, productListDTO, imageFiles);
//
//        // 수정된 상품 ID 반환
//        return ResponseEntity.ok(updatedProductPno);
//    }
//
//    // 상품 삭제
//    @PutMapping("/delete/{pno}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable Long pno) {
//        log.info("Received product deletion request for PNO {}", pno);
//        productService.deleteProduct(pno);
//        return ResponseEntity.ok().build();
//    }
//
//}
