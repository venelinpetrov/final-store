package com.vpe.finalstore.shipment.mappers;

import com.vpe.finalstore.shipment.dtos.CarrierDto;
import com.vpe.finalstore.shipment.entities.Carrier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    CarrierDto toDto(Carrier carrier);
}
