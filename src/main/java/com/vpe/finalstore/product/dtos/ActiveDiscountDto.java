package com.vpe.finalstore.product.dtos;

import com.vpe.finalstore.discount.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActiveDiscountDto {
	private DiscountType discountType;
	private BigDecimal value; // Percentage (0-100) or Fixed amount depending on discountType
	private LocalDateTime validUntil;
}
