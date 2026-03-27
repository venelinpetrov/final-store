package com.vpe.finalstore.shipment.mappers;

import com.vpe.finalstore.shipment.dtos.ShipmentDto;
import com.vpe.finalstore.shipment.entities.Shipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    ShipmentDto toDto(Shipment shipment);
}
