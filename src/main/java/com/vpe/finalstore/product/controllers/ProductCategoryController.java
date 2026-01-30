package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.ProductCategoryCreateDto;
import com.vpe.finalstore.product.dtos.ProductCategoryDto;
import com.vpe.finalstore.product.dtos.ProductCategoryUpdateDto;
import com.vpe.finalstore.product.mappers.ProductCategoryMapper;
import com.vpe.finalstore.product.repositories.ProductCategoryRepository;
import com.vpe.finalstore.product.services.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(
        summary = "Get all product categories"
    )
    @GetMapping
    public List<ProductCategoryDto> getAllCategories() {
        var categories = productCategoryRepository.getAllCategories();
        return productCategoryMapper.toDto(categories);
    }

    @Operation(
        summary = "Create a new product category"
    )
    @PostMapping
    public ResponseEntity<ProductCategoryDto> createCategory(@Valid @RequestBody ProductCategoryCreateDto dto) {
        var category = categoryService.createCategory(dto);

        return ResponseEntity
            .created(URI.create("/api/categories/" + category.getCategoryId()))
            .body(productCategoryMapper.toDto(category));
    }

    @Operation(
        summary = "Update a product category"
    )
    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@PathVariable Integer categoryId, @Valid @RequestBody ProductCategoryUpdateDto dto) {
        categoryService.updateCategory(categoryId, dto);

        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Delete a product category"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Integer categoryId) {
        productCategoryRepository.deleteById(categoryId);
    }
}
