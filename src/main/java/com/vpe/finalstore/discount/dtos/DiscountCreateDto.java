package com.vpe.finalstore.discount.dtos;

import com.vpe.finalstore.discount.enums.DiscountScopeType;
import com.vpe.finalstore.discount.enums.DiscountType;
import com.vpe.finalstore.discount.validation.ValidDiscount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@ValidDiscount
public class DiscountCreateDto {
    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private DiscountScopeType scope;

    @NotNull
    @PositiveOrZero
    private BigDecimal value;

    @PositiveOrZero
    private BigDecimal minOrderAmount;

    @PositiveOrZero
    private BigDecimal maxDiscountAmount;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private Boolean isActive;

    @Valid
    private Set<DiscountConditionCreateDto> discountConditions;

}