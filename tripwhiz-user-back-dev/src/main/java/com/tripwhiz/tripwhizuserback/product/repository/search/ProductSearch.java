package com.tripwhiz.tripwhizuserback.product.repository.search;

import com.tripwhiz.tripwhizuserback.common.dto.PageRequestDTO;
import com.tripwhiz.tripwhizuserback.common.dto.PageResponseDTO;
import com.tripwhiz.tripwhizuserback.product.dto.ProductListDTO;


public interface ProductSearch {

    //상품 필터링으로 목록 조회
    PageResponseDTO<ProductListDTO> findByFiltering(Long tno, Long cno, Long scno, PageRequestDTO pageRequestDTO);

    // 키워드 검색과 가격 필터링 추가(JH)
    PageResponseDTO<ProductListDTO> searchWithKeywordAndFilters(String keyword, Integer minPrice, Integer maxPrice,
                                                                Long tno, Long cno, Long scno, PageRequestDTO pageRequestDTO);

}