package com.tripwhiz.tripwhizuserback.category.repository;

import com.tripwhiz.tripwhizuserback.category.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    // 특정 상위 카테고리(cno)에 속한 하위 카테고리 조회
    List<SubCategory> findByCategory_Cno(Long cno);
}