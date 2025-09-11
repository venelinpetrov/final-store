package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}