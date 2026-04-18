package com.vpe.finalstore.shipment.services;

import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.shipment.entities.Shipment;
import com.vpe.finalstore.shipment.entities.ShipmentStatus;
import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;
import com.vpe.finalstore.shipment.enums.ShipmentStatusType;
import com.vpe.finalstore.shipment.repositories.ShipmentTrackingEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ShipmentTrackingEventService {
    private final ShipmentTrackingEventRepository shipmentTrackingEventRepository;

    public ShipmentTrackingEvent createEvent(Shipment shipment, ShipmentStatus status) {
        var prevEventOpt = shipmentTrackingEventRepository.getLatestEvent(shipment.getShipmentId());

        // Validate status transition (skip for first event)
        if (prevEventOpt.isPresent()) {
            validateStatusTransition(prevEventOpt.get().getStatus().getName(), status.getName());
        }

        var newEvent = new ShipmentTrackingEvent();
        newEvent.setShipment(shipment);
        newEvent.setStatus(status);
        newEvent.setEventDate(LocalDateTime.now());

        // TODO: location; description

        return shipmentTrackingEventRepository.save(newEvent);
    }

    public List<ShipmentTrackingEvent> getEventsForShipment(Integer shipmentId) {
        return shipmentTrackingEventRepository.getAllByShipmentShipmentIdOrderByEventDateDesc(shipmentId);
    }

    private void validateStatusTransition(ShipmentStatusType prevStatus, ShipmentStatusType nextStatus) {
        var validTransitions = switch (prevStatus) {
            case PENDING -> List.of(ShipmentStatusType.SHIPPED, ShipmentStatusType.CANCELED);
            case SHIPPED -> List.of(ShipmentStatusType.IN_TRANSIT, ShipmentStatusType.FAILED);
            case IN_TRANSIT -> List.of(ShipmentStatusType.DELIVERED, ShipmentStatusType.FAILED);
            case DELIVERED -> List.of(ShipmentStatusType.RETURNED);
            case CANCELED, RETURNED, FAILED -> List.of(); // Terminal states
        };

        if (!validTransitions.contains(nextStatus)) {
            throw new BadRequestException(
                String.format("Shipment cannot transition from %s to %s", prevStatus, nextStatus)
            );
        }
    }
}
