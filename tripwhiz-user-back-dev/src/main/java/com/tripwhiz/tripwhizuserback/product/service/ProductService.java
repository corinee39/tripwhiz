package com.tripwhiz.tripwhizuserback.product.service;

import com.tripwhiz.tripwhizuserback.category.domain.Category;
import com.tripwhiz.tripwhizuserback.category.domain.SubCategory;
import com.tripwhiz.tripwhizuserback.category.repository.CategoryRepository;
import com.tripwhiz.tripwhizuserback.category.repository.SubCategoryRepository;
import com.tripwhiz.tripwhizuserback.common.dto.PageRequestDTO;
import com.tripwhiz.tripwhizuserback.common.dto.PageResponseDTO;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.domain.ProductTheme;
import com.tripwhiz.tripwhizuserback.product.domain.ThemeCategory;
import com.tripwhiz.tripwhizuserback.product.dto.ProductListDTO;
import com.tripwhiz.tripwhizuserback.product.dto.ProductReadDTO;
import com.tripwhiz.tripwhizuserback.product.repository.ProductRepository;
import com.tripwhiz.tripwhizuserback.product.repository.ProductThemeRepository;
import com.tripwhiz.tripwhizuserback.product.repository.ThemeCategoryRepository;
import com.tripwhiz.tripwhizuserback.util.CustomFileUtil;
import com.tripwhiz.tripwhizuserback.util.file.domain.AttachFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ThemeCategoryRepository themeCategoryRepository;
    private final ProductThemeRepository productThemeRepository;
    private final CustomFileUtil customFileUtil;


    // 상품 ID로 단일 상품 조회
    public Optional<ProductReadDTO> getProductById(Long pno) {
        log.info("ID로 상품을 조회합니다: {}", pno);
        return productRepository.read(pno);
    }

    //상품 필터링
    public PageResponseDTO<ProductListDTO> searchProducts(Long tno, Long cno, Long scno, PageRequestDTO pageRequestDTO) {
        log.info("상품 목록을 조회합니다", tno, cno, scno);


        return productRepository.findByFiltering(tno, cno, scno, pageRequestDTO);
    }

    // 상품 키워드 검색 및 가격 필터링(JH)
    public PageResponseDTO<ProductListDTO> searchWithFilters(String keyword, Integer minPrice, Integer maxPrice,
                                                             Long tno, Long cno, Long scno, PageRequestDTO pageRequestDTO) {
        log.info("상품 키워드 검색 및 가격 필터링 실행 - keyword: {}, minPrice: {}, maxPrice: {}", keyword, minPrice, maxPrice);

        return productRepository.searchWithKeywordAndFilters(keyword, minPrice, maxPrice, tno, cno, scno, pageRequestDTO);
    }

    // 상품 생성
    public Long createProduct(ProductListDTO productListDTO, List<MultipartFile> imageFiles) throws IOException {

        log.info("productListDTO: {}", productListDTO);
        log.info("imageFiles: {}", imageFiles != null ? imageFiles.size() : 0);

        // Category와 SubCategory 조회
        Category category = categoryRepository.findById(productListDTO.getCno())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productListDTO.getCno()));
        SubCategory subCategory = subCategoryRepository.findById(productListDTO.getScno())
                .orElseThrow(() -> new RuntimeException("SubCategory not found with ID: " + productListDTO.getScno()));

        // Product 엔티티 생성 및 저장
        Product product = productListDTO.toEntity(category, subCategory);

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile imageFile = imageFiles.get(i);
            String savedImageName = customFileUtil.uploadProductImageFile(imageFile);

            // AttachFile 생성 (ord는 i + 1로 설정)
            AttachFile attachFile = new AttachFile(i + 1, savedImageName);
            product.addAttachFile(attachFile);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getPno());

        // 입력된 테마 카테고리(Tnos) 처리
        List<Long> tnos = productListDTO.getTnos();
        log.info("ThemeCategory IDs to process: {}", tnos);

        for (Long tno : tnos) {
            ThemeCategory themeCategory = themeCategoryRepository.findById(tno)
                    .orElseThrow(() -> new RuntimeException("ThemeCategory not found with ID: " + tno));

            // ProductTheme 생성 및 저장
            ProductTheme productTheme = ProductTheme.builder()
                    .product(savedProduct) // 현재 Product만 매핑
                    .themeCategory(themeCategory)
                    .build();
            productThemeRepository.save(productTheme);

            log.info("ProductTheme saved: Product ID = {}, ThemeCategory ID = {}", savedProduct.getPno(), tno);
        }

        return savedProduct.getPno();
    }



    // 상품 수정
    public Long updateProduct(Long pno, ProductListDTO productListDTO, List<MultipartFile> imageFiles) throws IOException {

        // Product 조회
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pno));

        // Category와 SubCategory를 조회
        Category category = categoryRepository.findById(productListDTO.getCno())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productListDTO.getCno()));
        SubCategory subCategory = subCategoryRepository.findById(productListDTO.getScno())
                .orElseThrow(() -> new RuntimeException("SubCategory not found with ID: " + productListDTO.getScno()));

        // updateFromDTO 호출
        product.updateFromDTO(productListDTO, category, subCategory);

        // 기존 AttachFile 삭제
        product.getAttachFiles().clear();

        // 새로운 이미지 파일 업로드 및 AttachFile 생성
        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile imageFile = imageFiles.get(i);
            String savedImageName = customFileUtil.uploadProductImageFile(imageFile);

            // AttachFile 생성 (ord는 i + 1로 설정)
            AttachFile attachFile = new AttachFile(i + 1, savedImageName);
            product.addAttachFile(attachFile);
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated with ID: {}", updatedProduct.getPno());

        return updatedProduct.getPno();
    }

    // 상품 삭제
    public void deleteProduct(Long pno) {
        // Product 조회
        Product product = productRepository.findById(pno)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pno));

        // 소프트 삭제 처리: delFlag를 true로 설정
        product.changeDelFlag(true);

        // 상품 수정 후 저장 (소프트 삭제 처리)
        productRepository.save(product);

        log.info("Product soft-deleted with ID: {}", pno);
    }
}



    // 상품 정보와 이미지를 함께 조회
//    public Optional<ProductReadDTO> getProductWithImage(Long pno) {
//        log.info("ID로 상품 및 이미지를 조회합니다: {}", pno);
//
//        return productRepository.read(pno).map(product -> {
//            // 이미지 URL 처리
//            if (product.getFileName() != null) {
//                product.setFileName("/uploads/" + product.getFileName());
//                log.info("이미지 URL 처리 완료: {}", product.getFileName());
//            }
//            return product;
//        });
//    }


// Admin API에서 전송된 상품 정보를 DB에 저장하는 메서드
//    public void saveProductFromAdmin(ProductListDTO productListDTO) {
//        log.info("Saving product from admin: {}", productListDTO);
//
//        // Category와 SubCategory를 찾음
//        Category category = categoryRepository.findById(productListDTO.getCategoryCno())
//                .orElseThrow(() -> new RuntimeException("Category not found for ID: " + productListDTO.getCategoryCno()));
//
//        SubCategory subCategory = subCategoryRepository.findById(productListDTO.getSubCategoryScno())
//                .orElseThrow(() -> new RuntimeException("SubCategory not found for ID: " + productListDTO.getSubCategoryScno()));
//
//        // ProductListDTO를 Product 엔티티로 변환 및 저장
//        Product product = productListDTO.toEntity(category, subCategory);
//        productRepository.save(product);
//
//        log.info("Product saved successfully: {}", product);
//    }

