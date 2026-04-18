package com.vpe.finalstore.discount.dtos;

import com.vpe.finalstore.discount.enums.DiscountConditionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DiscountConditionCreateDto {
    @NotNull
    private DiscountConditionType conditionType;

    @Positive
    private BigDecimal decimalValue;

    @Positive
    private Integer intValue;

    @Size(max = 255)
    private String stringValue;
}