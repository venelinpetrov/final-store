package com.vpe.finalstore.inventory.repositories;

import com.vpe.finalstore.inventory.entities.InventoryLevel;
import com.vpe.finalstore.inventory.entities.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {
}