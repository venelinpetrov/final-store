package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.Shipment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    @EntityGraph(attributePaths = {"carrier", "order", "order.customer"})
    @Query("SELECT s FROM Shipment s WHERE s.shipmentId = :shipmentId")
    Optional<Shipment> findShipmentWithDetails(@Param("shipmentId") Integer shipmentId);

    Optional<Shipment> getShipmentByShipmentId(Integer shipmentId);
}
