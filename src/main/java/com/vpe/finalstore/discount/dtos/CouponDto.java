package com.vpe.finalstore.discount.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponDto {
    private Integer couponId;
    private String code;
    private Integer discountId;
    private String discountName;
    private Integer usageLimit;
    private Integer timesUsed;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
