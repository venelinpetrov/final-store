package com.vpe.finalstore.inventory.repositories;

import com.vpe.finalstore.inventory.entities.InventoryLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryLevelRepository extends JpaRepository<InventoryLevel, Integer> {
    @Query(
        value = """
            SELECT i FROM InventoryLevel i
            JOIN FETCH i.variant v
            WHERE (:gte IS NULL OR i.quantityInStock >= :gte)
              AND (:lte IS NULL OR i.quantityInStock <= :lte)
        """,
        countQuery = """
            SELECT COUNT(i) FROM InventoryLevel i
            WHERE (:gte IS NULL OR i.quantityInStock >= :gte)
              AND (:lte IS NULL OR i.quantityInStock <= :lte)
        """
    )
    Page<InventoryLevel> findByQuantityInStockBetween(
        @Param("gte") Integer gte,
        @Param("lte") Integer lte,
        Pageable pageable
    );

    Optional<InventoryLevel> findByVariantVariantId(Integer variantVariantId);
}