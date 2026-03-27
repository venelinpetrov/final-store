package com.vpe.finalstore.shipment.repositories;

import com.vpe.finalstore.shipment.entities.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrierRepository extends JpaRepository<Carrier, Integer> {
}
