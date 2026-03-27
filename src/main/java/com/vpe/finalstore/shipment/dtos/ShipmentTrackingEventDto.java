package com.vpe.finalstore.shipment.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentTrackingEventDto {
    private Integer eventId;
    private ShipmentDto shipment;
    private ShipmentStatusDto status;
    private LocalDateTime eventDate;
    private String location;
    private String description;
}
