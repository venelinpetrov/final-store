package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.ProductCategoryCreateDto;
import com.vpe.finalstore.product.dtos.ProductCategoryDto;
import com.vpe.finalstore.product.dtos.ProductCategoryUpdateDto;
import com.vpe.finalstore.product.mappers.ProductCategoryMapper;
import com.vpe.finalstore.product.repositories.ProductCategoryRepository;
import com.vpe.finalstore.product.services.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {
    private final ProductCategoryService categoryService;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @GetMapping
    public List<ProductCategoryDto> getAllCategories() {
        var categories = productCategoryRepository.getAllCategories();
        return productCategoryMapper.toDto(categories);
    }

    @PostMapping
    public ResponseEntity<ProductCategoryDto> createCategory(@Valid @RequestBody ProductCategoryCreateDto dto) {
        var category = categoryService.createCategory(dto);

        return ResponseEntity
            .created(URI.create("/api/categories/" + category.getCategoryId()))
            .body(productCategoryMapper.toDto(category));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@PathVariable Integer categoryId, @Valid @RequestBody ProductCategoryUpdateDto dto) {
        categoryService.updateCategory(categoryId, dto);

        return ResponseEntity.ok().build();
    }
}
