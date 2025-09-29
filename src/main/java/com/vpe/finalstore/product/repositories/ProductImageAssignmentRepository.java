package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductImageAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductImageAssignmentRepository extends JpaRepository<ProductImageAssignment, Integer> {
    @Query("""
        SELECT p FROM ProductImageAssignment p
        WHERE p.product.productId = :productId AND p.image.imageId = :imageId
    """)
    Optional<ProductImageAssignment> findAssignment(@Param("imageId") Integer imageId, @Param("productId") Integer productId);
}