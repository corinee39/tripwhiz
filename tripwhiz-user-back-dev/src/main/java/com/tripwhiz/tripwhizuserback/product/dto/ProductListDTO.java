package com.tripwhiz.tripwhizuserback.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tripwhiz.tripwhizuserback.category.domain.Category;
import com.tripwhiz.tripwhizuserback.category.domain.SubCategory;
import com.tripwhiz.tripwhizuserback.product.domain.Product;
import com.tripwhiz.tripwhizuserback.util.file.domain.AttachFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListDTO {

    private Long pno;             // 상품 번호
    private String pname;          // 상품 이름
    private int price;
    private String pdesc;
    private Long cno;      // 상위 카테고리 ID
    private Long scno;  // 하위 카테고리 ID
    private List<Long> tnos;
    private List<AttachFile> attachFiles; // JH

    public Product toEntity(Category category, SubCategory subCategory) {
        return Product.builder()
                .pname(this.pname)
                .price(this.price)
                .pdesc(this.pdesc)
                .category(category)  // Category 객체를 직접 설정
                .subCategory(subCategory)  // SubCategory 객체를 직접 설정
                .attachFiles(this.attachFiles)
                .build();
    }
    public ProductListDTO(Long pno, String pdesc, String pname, int price, Long cno, Long scno) {
        this.pno = pno;
        this.pdesc = pdesc;
        this.pname = pname;
        this.price = price;
        this.cno = cno;
        this.scno = scno;
    }

}
