package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    @Query("""
        SELECT pc.categoryId AS categoryId,
               pc.name AS name,
               pc.parentCategory.categoryId AS parentCategoryId
        FROM ProductCategory pc
    """)
    List<ProductCategoryView> getAllCategories();
}
