package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Integer> {
}
