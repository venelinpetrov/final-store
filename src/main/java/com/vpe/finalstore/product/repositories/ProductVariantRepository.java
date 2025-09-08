package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.entities.ProductVariant;
import com.vpe.finalstore.product.entities.ProductVariantOptionValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    List<ProductVariant> findAllByProduct(Product product);

    @Query("""
        SELECT DISTINCT pv
        FROM ProductVariant pv
        JOIN FETCH pv.optionAssignments a
        JOIN FETCH a.value v
        JOIN FETCH v.option o
        WHERE pv.product.productId = :productId
    """)
    List<ProductVariant> getAllByProductId(@Param("productId") Integer productId);
}