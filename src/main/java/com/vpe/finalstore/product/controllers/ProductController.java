package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.repositories.ProductRepository;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam(value = "brandId", required = false) Integer brandId) {
        List<Product> productEntities;

        if (brandId != null) {
            productEntities = productRepository.findProductsByBrandBrandId(brandId);
        } else {
            productEntities = productRepository.findAll();
        }

        return productMapper.toDto(productEntities);
    }
}
