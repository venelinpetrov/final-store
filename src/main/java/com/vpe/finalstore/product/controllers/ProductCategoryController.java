package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.ProductCategoryDto;
import com.vpe.finalstore.product.mappers.ProductCategoryMapper;
import com.vpe.finalstore.product.repositories.ProductCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @GetMapping
    public List<ProductCategoryDto> getAllCategories() {
        var categories = productCategoryRepository.getAllCategories();
        return productCategoryMapper.toDto(categories);
    }
}
