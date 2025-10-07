package com.vpe.finalstore.inventory.mappers;

import com.vpe.finalstore.inventory.entities.InventoryMovement;
import com.vpe.finalstore.product.dtos.InventoryMovementDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMovementMapper {
    @Mapping(source = "variant.variantId", target = "variantId")
    InventoryMovementDto toDto(InventoryMovement movement);
}
