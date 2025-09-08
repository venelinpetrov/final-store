package com.vpe.finalstore.product.repositories;


import com.vpe.finalstore.product.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @EntityGraph(attributePaths = {"tags"})
    Page<Product> findProductsByBrandBrandId(Integer brandId, Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p")
    Page<Product> getAllWithTags(Pageable pageable);
}
