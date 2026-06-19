package com.tripwhiz.tripwhizuserback.category.service;

import com.tripwhiz.tripwhizuserback.category.domain.Category;
import com.tripwhiz.tripwhizuserback.category.domain.SubCategory;
import com.tripwhiz.tripwhizuserback.category.dto.CategoryDTO;
import com.tripwhiz.tripwhizuserback.category.dto.SubCategoryDTO;
import com.tripwhiz.tripwhizuserback.category.repository.CategoryRepository;
import com.tripwhiz.tripwhizuserback.category.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // 엔티티를 DTO로 변환하여 반환
        return categories.stream()
                .map(category -> new CategoryDTO(
                        category.getCno(),
                        category.getCname()
                ))
                .collect(Collectors.toList());
    }

    public List<SubCategoryDTO> getSubCategoriesByCategory(Long cno) {
        List<SubCategory> subCategories = subCategoryRepository.findByCategory_Cno(cno);

        // 엔티티를 DTO로 변환하여 반환
        return subCategories.stream()
                .map(subCategory -> new SubCategoryDTO(
                        subCategory.getScno(),
                        subCategory.getSname(),
                        subCategory.getCategory().getCno()))
                .collect(Collectors.toList());
    }

}
