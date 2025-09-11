package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductVariantImageAssignment;
import com.vpe.finalstore.product.entities.ProductVariantImageAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantImageAssignmentRepository extends JpaRepository<ProductVariantImageAssignment, ProductVariantImageAssignmentId> {
}