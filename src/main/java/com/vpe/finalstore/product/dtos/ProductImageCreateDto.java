package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductImageCreateDto {
    @NotNull
    private String link;

    @NotBlank
    private String altText;

    private Boolean isPrimary;
}
