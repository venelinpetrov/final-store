package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    Optional<Shipment> getShipmentByShipmentId(Integer shipmentId);
}
