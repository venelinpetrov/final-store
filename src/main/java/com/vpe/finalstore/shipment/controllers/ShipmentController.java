package com.vpe.finalstore.shipment.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.shipment.dtos.ShipmentTrackingEventDto;
import com.vpe.finalstore.shipment.mappers.ShipmentTrackingEventMapper;
import com.vpe.finalstore.shipment.repositories.ShipmentRepository;
import com.vpe.finalstore.shipment.services.ShipmentTrackingEventService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/shipments")
class ShipmentController {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingEventService trackingEventService;
    private final ShipmentTrackingEventMapper trackingEventMapper;

    @PreAuthorize("@shipmentSecurity.isOwner(#shipmentId, authentication)")
    @GetMapping("/{shipmentId}/tracking-events")
    public List<ShipmentTrackingEventDto> getShipmentTrackingEvents(@PathVariable Integer shipmentId) {
        var shipment = shipmentRepository.getShipmentByShipmentId(shipmentId)
            .orElseThrow(() -> new NotFoundException("Shipment not found"));

        var events = trackingEventService.getEventsForShipment(shipment.getShipmentId());

        return trackingEventMapper.toDto(events);
    }
}
