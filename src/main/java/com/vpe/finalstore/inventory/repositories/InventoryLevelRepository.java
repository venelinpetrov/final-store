package com.vpe.finalstore.inventory.repositories;

import com.vpe.finalstore.inventory.entities.InventoryLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryLevelRepository extends JpaRepository<InventoryLevel, Integer> {

    @Query("SELECT i FROM InventoryLevel i JOIN FETCH i.variant v")
    Page<InventoryLevel> findAllWithVariant(Pageable pageable);

    @Query("""
        SELECT i FROM InventoryLevel i
        JOIN FETCH i.variant v
        WHERE i.quantityInStock = 0
    """)
    Page<InventoryLevel> findByQuantityInStock(Integer quantityInStock, Pageable pageable);
}