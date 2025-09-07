package com.vpe.finalstore.product.repositories;


import com.vpe.finalstore.product.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @EntityGraph(attributePaths = {"tags"})
    List<Product> findProductsByBrandBrandId(Integer brandId);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p")
    List<Product> getAllWithTags();
}
