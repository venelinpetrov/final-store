package com.vpe.finalstore.discount.dtos;

import com.vpe.finalstore.discount.enums.DiscountConditionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountConditionDto {
    private Integer conditionId;
    private DiscountConditionType conditionType;
    private BigDecimal decimalValue;
    private Integer intValue;
    private String stringValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}