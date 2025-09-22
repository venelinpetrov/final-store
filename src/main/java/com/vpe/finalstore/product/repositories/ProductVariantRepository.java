package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductVariant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    @EntityGraph(attributePaths = {
        "optionAssignments",
        "optionAssignments.value",
        "optionAssignments.value.option",
        "images"
    })
    Optional<List<ProductVariant>> findProductVariantsByProductProductIdAndIsArchivedIsFalse(Integer productProductId);

    Optional<ProductVariant> findByVariantIdAndIsArchivedIsTrue(Integer variantId);
}