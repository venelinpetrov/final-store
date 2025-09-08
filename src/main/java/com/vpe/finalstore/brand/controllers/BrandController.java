package com.vpe.finalstore.brand.controllers;

import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping("/{brandId}/products")
    public Page<ProductDto> getProducts(
        @PathVariable("brandId") Integer brandId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findProductsByBrandBrandId(brandId, pageable);
        List<ProductDto> dtos = productMapper.toDto(products.getContent());

        return new PageImpl<>(dtos,pageable, products.getTotalElements());
    }
}
