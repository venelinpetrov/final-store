package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductVariantDto {
    private Integer variantId;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantityInStock;
    private List<ProductVariantOptionAssignmentDto> options;
}
