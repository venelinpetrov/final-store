package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
}