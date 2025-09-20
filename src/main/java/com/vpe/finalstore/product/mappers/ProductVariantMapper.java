package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.ProductImageDto;
import com.vpe.finalstore.product.dtos.ProductVariantDto;
import com.vpe.finalstore.product.dtos.ProductVariantOptionAssignmentDto;
import com.vpe.finalstore.product.entities.ProductVariant;
import com.vpe.finalstore.product.entities.ProductVariantImageAssignment;
import com.vpe.finalstore.product.entities.ProductVariantOptionAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    @Mapping(source = "optionAssignments", target = "options")
    ProductVariantDto toDto(ProductVariant variant);

    List<ProductVariantDto> toDto(Set<ProductVariant> variants);

    @Mapping(source = "value.option.optionId", target = "optionId")
    @Mapping(source = "value.option.name", target = "optionName")
    @Mapping(source = "value.valueId", target = "valueId")
    @Mapping(source = "value.value", target = "value")
    ProductVariantOptionAssignmentDto toDto(ProductVariantOptionAssignment assignment);


    default Set<ProductImageDto> mapProductVariantImages(Set<ProductVariantImageAssignment> images) {
        if (images == null) {
            return Set.of();
        }
        return images.stream()
            .map((imageAssignment) -> new ProductImageDto(
                imageAssignment.getImage().getImageId(),
                imageAssignment.getImage().getLink(),
                imageAssignment.getImage().getAltText(),
                imageAssignment.getIsPrimary()
            ))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
