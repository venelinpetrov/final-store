package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryUpdateDto {
    @NotBlank
    private String name;

    private Integer parentCategoryId;
}
