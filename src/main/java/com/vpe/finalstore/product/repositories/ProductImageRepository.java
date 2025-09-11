package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
}