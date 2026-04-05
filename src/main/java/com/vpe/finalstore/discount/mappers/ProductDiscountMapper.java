package com.vpe.finalstore.discount.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vpe.finalstore.discount.dtos.ProductDiscountDto;
import com.vpe.finalstore.discount.entities.ProductDiscount;

@Mapper(componentModel = "spring")
public interface ProductDiscountMapper {
	@Mapping(target = "variantId", source = "productVariant.variantId")
	ProductDiscountDto toDto(ProductDiscount discount);
}
