package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariantOptionAssignmentCreateDto {
    @NotBlank
    private String name;

    @NotNull
    private String value;
}
