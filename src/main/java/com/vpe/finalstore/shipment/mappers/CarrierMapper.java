package com.vpe.finalstore.shipment.mappers;

import com.vpe.finalstore.shipment.dtos.CarrierDto;
import com.vpe.finalstore.shipment.entities.Carrier;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    CarrierDto toDto(Carrier carrier);
    List<CarrierDto> toDto(List<Carrier> carriers);
}
