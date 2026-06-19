package com.tripwhiz.tripwhizuserback.category.repository;

import com.tripwhiz.tripwhizuserback.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
