package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductVariantOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariantOptionRepository extends JpaRepository<ProductVariantOption, Integer> {
    Optional<ProductVariantOption> findByNameIgnoreCase(String name);
}