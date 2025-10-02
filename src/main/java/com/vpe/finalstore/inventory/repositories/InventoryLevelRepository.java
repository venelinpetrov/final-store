package com.vpe.finalstore.inventory.repositories;

import com.vpe.finalstore.inventory.entities.InventoryLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryLevelRepository extends JpaRepository<InventoryLevel, Integer> {

    @Query(
        value = "SELECT i FROM InventoryLevel i JOIN FETCH i.variant v",
        countQuery = "SELECT COUNT(i) FROM InventoryLevel i"
    )
    Page<InventoryLevel> getAll(Pageable p);

    List<InventoryLevel> getAllByQuantityInStock(Integer quantityInStock);
}