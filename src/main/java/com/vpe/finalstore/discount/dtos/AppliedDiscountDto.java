package com.vpe.finalstore.discount.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppliedDiscountDto {
	private Integer appliedDiscountId;
	private Integer orderId;
	private Integer orderItemId;
	private Integer discountId;
	private Integer couponId;
	private BigDecimal discountAmount;
	private LocalDateTime appliedAt;
}
