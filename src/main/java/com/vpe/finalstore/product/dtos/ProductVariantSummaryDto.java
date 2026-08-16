package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ProductVariantSummaryDto {
	private Integer variantId;
	private BigDecimal unitPrice;
	private Set<ProductImageDto> images;
	private ActiveDiscountDto discount;
	private List<ProductVariantOptionAssignmentDto> options;
}
