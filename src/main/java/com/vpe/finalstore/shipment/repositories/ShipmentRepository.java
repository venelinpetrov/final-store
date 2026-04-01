package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
}
