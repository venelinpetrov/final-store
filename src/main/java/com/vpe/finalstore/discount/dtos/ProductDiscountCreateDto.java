package com.vpe.finalstore.discount.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDiscountCreateDto {
	@NonNull
	private Integer variantId;

	@NonNull
	private BigDecimal discountPercentage;

	@NonNull
	private LocalDateTime validFrom;

	@NonNull
	private LocalDateTime validUntil;
}
