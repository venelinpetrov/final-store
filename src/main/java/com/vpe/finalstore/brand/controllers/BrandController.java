package com.vpe.finalstore.brand.controllers;

import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping("/{brandId}/products")
    public List<ProductDto> getProducts(@PathVariable("brandId") Integer brandId) {
        List<Product> productEntities = productRepository.findProductsByBrandBrandId(brandId);

        return productMapper.toDto(productEntities);
    }
}
