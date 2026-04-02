package com.vpe.finalstore.shipment.controllers;

import com.vpe.finalstore.shipment.dtos.ShipmentDto;
import com.vpe.finalstore.shipment.dtos.ShipmentTrackingEventDto;
import com.vpe.finalstore.shipment.mappers.ShipmentMapper;
import com.vpe.finalstore.shipment.mappers.ShipmentTrackingEventMapper;
import com.vpe.finalstore.shipment.services.ShipmentService;
import com.vpe.finalstore.shipment.services.ShipmentTrackingEventService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final ShipmentTrackingEventService trackingEventService;
    private final ShipmentTrackingEventMapper trackingEventMapper;
    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;

    @PreAuthorize("@shipmentSecurity.isOwner(#shipmentId, authentication)")
    @GetMapping("/{shipmentId}/tracking-events")
    public ResponseEntity<List<ShipmentTrackingEventDto>> getShipmentTrackingEvents(@PathVariable Integer shipmentId) {
        var events = trackingEventService.getEventsForShipment(shipmentId);

        return ResponseEntity.ok(trackingEventMapper.toDto(events));
    }

    @PreAuthorize("@shipmentSecurity.isOwner(#shipmentId, authentication)")
    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentDto> getShipment(@PathVariable Integer shipmentId) {
        var shipment = shipmentService.getShipmentDetail(shipmentId);
        var status = shipmentService.getShipmentStatus(shipmentId);

        var dto = shipmentMapper.toDto(shipment);

        dto.setStatus(status);

        return ResponseEntity.ok(dto);
    }
}
