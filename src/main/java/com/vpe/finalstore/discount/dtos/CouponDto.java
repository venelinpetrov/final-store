package com.vpe.finalstore.discount.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponDto {
	private Integer couponId;
	private String code;
	private Integer discountId;
	private Integer usageLimit;
}
