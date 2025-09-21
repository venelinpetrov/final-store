package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    Optional<ProductImage> findByLink(String link);
}