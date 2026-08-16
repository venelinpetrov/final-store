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
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "quantityInStock", ignore = true)
    ProductVariantDto toDto(ProductVariant variant);

    List<ProductVariantDto> toDto(List<ProductVariant> variants);

    @Mapping(target = "value", source = "value.value")
    @Mapping(target = "valueId", source = "value.valueId")
    @Mapping(target = "optionName", source = "value.option.name")
    @Mapping(target = "optionId", source = "value.option.optionId")
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
