package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductVariantImageAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductVariantImageAssignmentRepository extends JpaRepository<ProductVariantImageAssignment, Integer> {
    @Query("""
        SELECT v FROM ProductVariantImageAssignment v
        WHERE v.variant.variantId = :variantId AND v.image.imageId = :imageId
    """)
    Optional<ProductVariantImageAssignment> findAssignment(@Param("imageId") Integer imageId, @Param("variantId") Integer variantId);
}