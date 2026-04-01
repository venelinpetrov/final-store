package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.ShipmentStatus;
import com.vpe.finalstore.shipment.enums.ShipmentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Integer> {
    Optional<ShipmentStatus> findByName(ShipmentStatusType name);
}
