package com.tripwhiz.tripwhizuserback.product.repository;

import com.tripwhiz.tripwhizuserback.product.domain.ProductTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductThemeRepository extends JpaRepository<ProductTheme, Long> {
}
