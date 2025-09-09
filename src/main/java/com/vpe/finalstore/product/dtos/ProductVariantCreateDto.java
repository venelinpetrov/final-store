package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductVariantCreateDto {
    @NotBlank
    private String sku;

    @NotNull
    private BigDecimal unitPrice;

    private Integer quantityInStock;

    private List<ProductVariantOptionAssignmentCreateDto> options;
}
