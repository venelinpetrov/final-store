package com.vpe.finalstore.product.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActiveDiscountDto {
	private BigDecimal percentage;
	private LocalDateTime validUntil;
}
