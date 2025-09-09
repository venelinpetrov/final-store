package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductVariantOption;
import com.vpe.finalstore.product.entities.ProductVariantOptionValue;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariantOptionValueRepository extends JpaRepository<ProductVariantOptionValue, Integer> {
    Optional<ProductVariantOptionValue> findByOptionAndValue(ProductVariantOption option, @NotNull String value);
}