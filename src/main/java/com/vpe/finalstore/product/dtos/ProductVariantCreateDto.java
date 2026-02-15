package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProductVariantCreateDto {
    @NotBlank
    private String sku;

    @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
    private BigDecimal unitPrice;

    @NotNull
    private short quantityInStock;

    @NotNull
    private Boolean isArchived;

    @NotNull
    private List<ProductVariantOptionAssignmentCreateDto> options;

    @NotNull
    private Set<ProductImageCreateDto> images;
}
