package com.vpe.finalstore.inventory.mappers;

import com.vpe.finalstore.inventory.dtos.InventoryItemDto;
import com.vpe.finalstore.inventory.entities.InventoryLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryLevelMapper {
    @Mapping(source = "variant.sku", target = "sku")
    @Mapping(source = "variant.variantId", target = "variantId")
    InventoryItemDto toDto(InventoryLevel item);

    List<InventoryItemDto> toDto(List<InventoryLevel> item);
}
