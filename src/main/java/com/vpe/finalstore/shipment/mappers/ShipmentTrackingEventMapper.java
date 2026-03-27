package com.vpe.finalstore.shipment.mappers;

import com.vpe.finalstore.shipment.dtos.ShipmentTrackingEventDto;
import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentTrackingEventMapper {
    ShipmentTrackingEventDto toDto(ShipmentTrackingEvent trackingEvent);
}
