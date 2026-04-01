package com.vpe.finalstore.shipment.services;

import com.vpe.finalstore.shipment.entities.Shipment;
import com.vpe.finalstore.shipment.entities.ShipmentStatus;
import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;
import com.vpe.finalstore.shipment.repositories.ShipmentTrackingEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ShipmentTrackingEventService {
    private final ShipmentTrackingEventRepository shipmentTrackingEventRepository;

    public void createEvent(Shipment shipment, ShipmentStatus status) {
        var event = new ShipmentTrackingEvent();

        event.setShipment(shipment);
        event.setStatus(status);
        event.setEventDate(LocalDateTime.now());

        shipmentTrackingEventRepository.save(event);

        // TODO: location; description
    }

    public List<ShipmentTrackingEvent> getEventsForShipment(Integer shipmentId) {
        return shipmentTrackingEventRepository.getAllByEventId(shipmentId);
    }
}
