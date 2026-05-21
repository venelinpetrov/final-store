package com.vpe.finalstore.discount.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCouponDto {
    @NotNull
    @Size(min = 3, max = 50)
    private String code;
}
