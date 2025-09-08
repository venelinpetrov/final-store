package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.ProductVariantDto;
import com.vpe.finalstore.product.dtos.ProductVariantOptionAssignmentDto;
import com.vpe.finalstore.product.entities.ProductVariant;
import com.vpe.finalstore.product.entities.ProductVariantOptionAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    @Mapping(source = "optionAssignments", target = "options")
    ProductVariantDto toDto(ProductVariant variant);

    List<ProductVariantDto> toDto(List<ProductVariant> variants);

    @Mapping(source = "value.option.optionId", target = "optionId")
    @Mapping(source = "value.option.name", target = "optionName")
    @Mapping(source = "value.valueId", target = "valueId")
    @Mapping(source = "value.value", target = "value")
    ProductVariantOptionAssignmentDto toDto(ProductVariantOptionAssignment assignment);
}
