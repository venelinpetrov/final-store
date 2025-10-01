package com.vpe.finalstore.product.services;

import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.dtos.ProductCategoryCreateDto;
import com.vpe.finalstore.product.dtos.ProductCategoryUpdateDto;
import com.vpe.finalstore.product.entities.ProductCategory;
import com.vpe.finalstore.product.mappers.ProductCategoryMapper;
import com.vpe.finalstore.product.repositories.ProductCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategory createCategory(ProductCategoryCreateDto dto) {
        ProductCategory parentCategory = null;
        if (dto.getParentCategoryId() != null) {
            parentCategory = productCategoryRepository.findById(dto.getParentCategoryId())
                .orElseThrow(() -> new NotFoundException("Parent category not found"));
        }

        var category = new ProductCategory();
        category.setName(dto.getName());
        category.setParentCategory(parentCategory);

        validateParentCategory(category, parentCategory);

        return productCategoryRepository.save(category);
    }

    public void updateCategory(Integer categoryId, ProductCategoryUpdateDto dto) {
        var category = productCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Category not found"));

        ProductCategory parentCategory = null;
        if (dto.getParentCategoryId() != null) {
            parentCategory = productCategoryRepository.findById(dto.getParentCategoryId())
                .orElseThrow(() -> new NotFoundException("Parent category not found"));
        }

        category.setName(dto.getName());
        category.setParentCategory(parentCategory);

        validateParentCategory(category, parentCategory);

        productCategoryRepository.save(category);
    }

    private void validateParentCategory(ProductCategory category, ProductCategory parentCategory) {
        if (parentCategory == null) {
            return;
        }

        if (category.getCategoryId().equals(parentCategory.getCategoryId())) {
            throw new BadRequestException("A category cannot be its own parent.");
        }

        ProductCategory current = parentCategory;
        while(current != null) {
            if (current.getCategoryId().equals(category.getCategoryId())) {
                throw new BadRequestException("Category hierarchy cannot contain cycles.");
            }
            current = current.getParentCategory();
        }
    }
}
