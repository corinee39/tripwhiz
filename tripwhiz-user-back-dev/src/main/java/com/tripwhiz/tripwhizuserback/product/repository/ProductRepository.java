package com.tripwhiz.tripwhizuserback.product.repository;


import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.dto.ProductListDTO;
import com.tripwhiz.tripwhizuserback.product.dto.ProductReadDTO;
import com.tripwhiz.tripwhizuserback.product.repository.search.ProductSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearch {

    // 특정 상품을 ProductReadDTO 형태로 조회
    @Query("select new com.tripwhiz.tripwhizuserback.product.dto.ProductReadDTO(" +
            "p.pno, p.pname, p.pdesc, p.price, " +
            "p.category.cno, p.subCategory.scno) " +
            "from Product p " +
            "where p.pno = :pno")
    Optional<ProductReadDTO> read(@Param("pno") Long pno);

 }
