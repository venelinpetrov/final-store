package com.vpe.finalstore.shipment.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.shipment.dtos.ShipmentDto;
import com.vpe.finalstore.shipment.dtos.ShipmentTrackingEventDto;
import com.vpe.finalstore.shipment.mappers.ShipmentMapper;
import com.vpe.finalstore.shipment.mappers.ShipmentTrackingEventMapper;
import com.vpe.finalstore.shipment.repositories.ShipmentStatusRepository;
import com.vpe.finalstore.shipment.services.ShipmentService;
import com.vpe.finalstore.shipment.services.ShipmentTrackingEventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/shipments")
class ShipmentController {
    private final ShipmentTrackingEventService trackingEventService;
    private final ShipmentTrackingEventMapper trackingEventMapper;
    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;
    private final ShipmentStatusRepository shipmentStatusRepository;

    @Operation(
        summary = "Get shipment tracking events",
        description = "The events are ordered in descending order"
    )
    @PreAuthorize("@shipmentSecurity.isOwner(#shipmentId, authentication)")
    @GetMapping("/{shipmentId}/tracking-events")
    public ResponseEntity<List<ShipmentTrackingEventDto>> getShipmentTrackingEvents(@PathVariable Integer shipmentId) {
        var events = trackingEventService.getEventsForShipment(shipmentId);

        return ResponseEntity.ok(trackingEventMapper.toDto(events));
    }

    @Operation(
        summary = "Get shipment detail"
    )
    @PreAuthorize("@shipmentSecurity.isOwner(#shipmentId, authentication)")
    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentDto> getShipment(@PathVariable Integer shipmentId) {
        var shipment = shipmentService.getShipmentDetail(shipmentId);
        var status = shipmentService.getShipmentStatus(shipmentId);

        var dto = shipmentMapper.toDto(shipment);

        dto.setStatus(status);

        return ResponseEntity.ok(dto);
    }

    @Operation(
        summary = "Create tracking event (Admin only)"
    )
    @PostMapping("/{shipmentId}/tracking-events")
    public ResponseEntity<ShipmentTrackingEventDto> createTrackingEvent(
        @PathVariable Integer shipmentId,
        @RequestParam Integer statusId,
        UriComponentsBuilder uriBuilder
    ) {
        var shipment = shipmentService.getShipmentDetail(shipmentId);
        var status = shipmentStatusRepository.findByStatusId(statusId)
            .orElseThrow(() -> new NotFoundException("Shipment status not found"));

        var event = trackingEventService.createEvent(shipment, status);

        var uri = uriBuilder
            .path("/api/shipments/{shipmentId}/tracking-events")
            .buildAndExpand(shipmentId)
            .toUri();

        var eventDto = trackingEventMapper.toDto(event);

        return ResponseEntity.created(uri).body(eventDto);
    }
}
