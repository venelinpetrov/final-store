package com.vpe.finalstore.discount.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDiscountDto {
	private Integer discountId;
	private Integer variantId;
	private BigDecimal discountPercentage;
	private LocalDateTime validFrom;
	private LocalDateTime validUntil;
}
