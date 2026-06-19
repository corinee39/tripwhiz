package com.tripwhiz.tripwhizuserback.product.repository;

import com.tripwhiz.tripwhizuserback.product.domain.ThemeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeCategoryRepository extends JpaRepository<ThemeCategory, Long> {
}
