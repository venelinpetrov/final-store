package com.vpe.finalstore.product.repositories;


import com.vpe.finalstore.product.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
