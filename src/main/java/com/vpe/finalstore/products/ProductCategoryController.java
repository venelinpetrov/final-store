package com.vpe.finalstore.products;

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
