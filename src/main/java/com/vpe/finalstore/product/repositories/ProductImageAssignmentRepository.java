package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductImageAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageAssignmentRepository extends JpaRepository<ProductImageAssignment, Integer> {
}