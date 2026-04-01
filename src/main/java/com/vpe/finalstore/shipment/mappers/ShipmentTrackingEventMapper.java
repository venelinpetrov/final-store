package com.vpe.finalstore.shipment.mappers;

import com.vpe.finalstore.shipment.dtos.ShipmentTrackingEventDto;
import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShipmentTrackingEventMapper {
    @Mapping(target = "status", source = "status.name")
    ShipmentTrackingEventDto toDto(ShipmentTrackingEvent trackingEvent);

    List<ShipmentTrackingEventDto> toDto(List<ShipmentTrackingEvent> events);
}
