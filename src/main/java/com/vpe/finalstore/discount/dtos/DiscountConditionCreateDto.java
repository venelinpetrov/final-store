package com.vpe.finalstore.discount.dtos;

import com.vpe.finalstore.discount.enums.DiscountConditionType;
import com.vpe.finalstore.discount.validation.ValidDiscountCondition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ValidDiscountCondition
public class DiscountConditionCreateDto {
    @NotNull
    private DiscountConditionType conditionType;

    private BigDecimal decimalValue;

    private Integer intValue;

    @Size(max = 255)
    private String stringValue;
}