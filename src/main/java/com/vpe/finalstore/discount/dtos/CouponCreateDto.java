package com.vpe.finalstore.discount.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponCreateDto {
	@NotBlank
	private String code;

	@NotNull
	private Integer discountId;

	@NotNull
	private Integer usageLimit;
}
