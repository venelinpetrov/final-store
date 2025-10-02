package com.vpe.finalstore.inventory.repositories;

import com.vpe.finalstore.inventory.entities.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {
}