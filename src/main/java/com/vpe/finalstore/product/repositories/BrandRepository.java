package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.brand.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}