package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Integer> {
    List<ShipmentTrackingEvent> getAllByEventId(Integer eventId);
}
