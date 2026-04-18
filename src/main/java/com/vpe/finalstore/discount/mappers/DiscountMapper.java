package com.vpe.finalstore.discount.mappers;

import com.vpe.finalstore.discount.dtos.DiscountConditionDto;
import com.vpe.finalstore.discount.dtos.DiscountCreateDto;
import com.vpe.finalstore.discount.dtos.DiscountDto;
import com.vpe.finalstore.discount.entities.Discount;
import com.vpe.finalstore.discount.entities.DiscountCondition;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DiscountMapper {
    Discount toEntity(DiscountCreateDto discountCreateDto);

    @AfterMapping
    default void linkDiscountConditions(@MappingTarget Discount discount) {
        discount.getDiscountConditions().forEach(discountCondition -> discountCondition.setDiscount(discount));
    }

    DiscountDto toDto(Discount discount);

    DiscountConditionDto toDto(DiscountCondition discountCondition);

}