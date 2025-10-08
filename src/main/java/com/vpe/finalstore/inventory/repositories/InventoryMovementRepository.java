package com.vpe.finalstore.inventory.repositories;

import com.vpe.finalstore.inventory.entities.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {
    Page<InventoryMovement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<InventoryMovement> findByCreatedAtAfter(LocalDateTime from, Pageable pageable);
    Page<InventoryMovement> findByCreatedAtBefore(LocalDateTime to, Pageable pageable);
}