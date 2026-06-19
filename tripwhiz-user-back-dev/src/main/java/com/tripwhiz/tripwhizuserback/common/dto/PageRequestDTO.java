package com.tripwhiz.tripwhizuserback.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    @Min(value = 1, message = "over 1")
    private int page = 1;

    @Builder.Default
    @Min(value = 10, message = "set over 10")
    @Max(value = 100, message = "cannot over 100")
    private int size = 10;


    // Pageable 객체 생성 메서드 추가
    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }
}


