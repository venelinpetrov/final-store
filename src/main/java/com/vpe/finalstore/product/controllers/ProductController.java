package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<ProductDto> getProducts(
        @RequestParam(value = "brandId", required = false) Integer brandId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (brandId != null) {
            products = productRepository.findProductsByBrandBrandId(brandId, pageable);
        } else {
            products = productRepository.getAllWithTags(pageable);
        }

        List<ProductDto> dtos = productMapper.toDto(products.getContent());

        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }
}
