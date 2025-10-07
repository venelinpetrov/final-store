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
        JOIN FETCH v.product p
        LEFT JOIN FETCH p.categories c
        WHERE (:sku IS NULL OR v.sku LIKE CONCAT('%', :sku, '%'))
          AND (:productName IS NULL OR p.name LIKE CONCAT('%', :productName, '%'))
          AND (:categoryId IS NULL OR c.categoryId = :categoryId)
          AND (:gte IS NULL OR i.quantityInStock >= :gte)
          AND (:lte IS NULL OR i.quantityInStock <= :lte)
    """,
        countQuery = """
        SELECT COUNT(i) FROM InventoryLevel i
        JOIN i.variant v
        JOIN v.product p
        LEFT JOIN p.categories c
        WHERE (:sku IS NULL OR v.sku LIKE CONCAT('%', :sku, '%'))
          AND (:productName IS NULL OR p.name LIKE CONCAT('%', :productName, '%'))
          AND (:categoryId IS NULL OR c.categoryId = :categoryId)
          AND (:gte IS NULL OR i.quantityInStock >= :gte)
          AND (:lte IS NULL OR i.quantityInStock <= :lte)
    """
    )
    Page<InventoryLevel> searchInventoryLevels(
        @Param("sku") String sku,
        @Param("productName") String productName,
        @Param("categoryId") Integer categoryId,
        @Param("gte") Integer gte,
        @Param("lte") Integer lte,
        Pageable pageable
    );

    Optional<InventoryLevel> findByVariantVariantId(Integer variantVariantId);

    @Query("""
        SELECT i FROM InventoryLevel i
        WHERE i.variant.sku = :sku
    """)
    Optional<InventoryLevel> findBySku( String sku);
}