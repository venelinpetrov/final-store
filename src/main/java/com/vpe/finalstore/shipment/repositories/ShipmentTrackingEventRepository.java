package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.ShipmentTrackingEvent;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Integer> {
    @EntityGraph(attributePaths = {"status"})
    @Query("""
        SELECT ste FROM ShipmentTrackingEvent ste
        WHERE ste.shipment.shipmentId = :shipmentId
        ORDER BY ste.eventDate DESC
        LIMIT 1
    """)
    ShipmentTrackingEvent getLatestEvent(@Param("shipmentId") Integer shipmentShipmentId);

    List<ShipmentTrackingEvent> getAllByShipmentShipmentIdOrderByEventDateDesc(Integer shipmentShipmentId);
}
