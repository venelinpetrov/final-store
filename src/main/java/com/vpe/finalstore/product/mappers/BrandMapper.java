package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.BrandDto;
import com.vpe.finalstore.product.entities.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand brand);
}
