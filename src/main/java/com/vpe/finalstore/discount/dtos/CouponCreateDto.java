package com.vpe.finalstore.discount.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponCreateDto {
    @NotNull
    @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters")
    private String code;

    @NotNull
    private Integer discountId;

    @Positive(message = "Usage limit must be greater than 0 or null for unlimited")
    private Integer usageLimit;

    private Boolean isActive = true;
}
