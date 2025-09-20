package com.vpe.finalstore.product.repositories;


import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.entities.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
        SELECT p FROM Product p
        JOIN FETCH p.variants v
        JOIN FETCH v.optionAssignments oa
        JOIN FETCH oa.value val
        JOIN FETCH val.option
        WHERE p.productId = :productId
    """)
    Optional<Product> getWithVariantsByProductId(@Param("productId") Integer productId);

    @EntityGraph(attributePaths = {"tags"})
    Page<Product> findProductsByBrandBrandId(Integer brandId, Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p")
    Page<Product> getAllWithTags(Pageable pageable);

    @Query("""
        SELECT p FROM Product p
            LEFT JOIN FETCH p.tags t
            LEFT JOIN FETCH p.categories c
            LEFT JOIN FETCH p.brand b
            LEFT JOIN FETCH p.images i
            WHERE t IN :tags
        """
    )
    Page<Product> getByAnyTagsIn(@Param("tags") Set<Tag> tags, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            JOIN p.tags t
            WHERE t IN :tags
            GROUP BY p HAVING COUNT(t) = :tagCount
        """
    )
    Page<Product> getByAllTagsIn(@Param("tags") Set<Tag> tags, @Param("tagCount") long tagCount, Pageable pageable);
}
