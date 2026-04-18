package com.vpe.finalstore.discount.dtos;

import com.vpe.finalstore.discount.enums.DiscountScopeType;
import com.vpe.finalstore.discount.enums.DiscountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DiscountDto {
    private Integer discountId;
    private String name;
    private DiscountType discountType;
    private DiscountScopeType scope;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DiscountConditionDto> discountConditions;
}