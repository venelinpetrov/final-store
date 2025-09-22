package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @NonNull
    @Override
    Page<Brand> findAll(@NonNull Pageable pageable);
}