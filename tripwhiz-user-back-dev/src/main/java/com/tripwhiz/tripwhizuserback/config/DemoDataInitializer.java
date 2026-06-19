package com.tripwhiz.tripwhizuserback.config;

import com.tripwhiz.tripwhizuserback.category.domain.Category;
import com.tripwhiz.tripwhizuserback.category.domain.SubCategory;
import com.tripwhiz.tripwhizuserback.category.repository.CategoryRepository;
import com.tripwhiz.tripwhizuserback.category.repository.SubCategoryRepository;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.product.domain.ProductTheme;
import com.tripwhiz.tripwhizuserback.product.domain.ThemeCategory;
import com.tripwhiz.tripwhizuserback.product.repository.ProductRepository;
import com.tripwhiz.tripwhizuserback.product.repository.ProductThemeRepository;
import com.tripwhiz.tripwhizuserback.product.repository.ThemeCategoryRepository;
import com.tripwhiz.tripwhizuserback.util.file.domain.AttachFile;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DemoDataInitializer {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ThemeCategoryRepository themeCategoryRepository;
    private final ProductRepository productRepository;
    private final ProductThemeRepository productThemeRepository;

    @Bean
    @Transactional
    CommandLineRunner seedDemoData() {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            Category travelGoods = categoryRepository.save(Category.builder().cname("Travel Goods").build());
            Category localFood = categoryRepository.save(Category.builder().cname("Local Food").build());
            Category lifestyle = categoryRepository.save(Category.builder().cname("Lifestyle").build());

            SubCategory bags = subCategoryRepository.save(SubCategory.builder().sname("Bags").category(travelGoods).build());
            SubCategory snacks = subCategoryRepository.save(SubCategory.builder().sname("Snacks").category(localFood).build());
            SubCategory wellness = subCategoryRepository.save(SubCategory.builder().sname("Wellness").category(lifestyle).build());

            ThemeCategory featured = themeCategoryRepository.save(ThemeCategory.builder().tname("Featured").build());
            ThemeCategory healing = themeCategoryRepository.save(ThemeCategory.builder().tname("Healing").build());

            saveProduct("Carry-on Organizer", "Compact organizer for short trips.", 29000, travelGoods, bags, featured);
            saveProduct("Local Snack Set", "A curated snack pack for travelers.", 18000, localFood, snacks, featured);
            saveProduct("Travel Comfort Kit", "Simple comfort items for long-distance travel.", 24000, lifestyle, wellness, healing);
            saveProduct("Light Day Pack", "Lightweight bag for city tours.", 39000, travelGoods, bags, featured);
        };
    }

    private void saveProduct(
            String name,
            String description,
            int price,
            Category category,
            SubCategory subCategory,
            ThemeCategory themeCategory
    ) {
        Product product = Product.builder()
                .pname(name)
                .pdesc(description)
                .price(price)
                .category(category)
                .subCategory(subCategory)
                .attachFiles(List.of(new AttachFile(1, "m2.jpg")))
                .build();

        Product savedProduct = productRepository.save(product);

        productThemeRepository.save(ProductTheme.builder()
                .product(savedProduct)
                .themeCategory(themeCategory)
                .build());
    }
}
