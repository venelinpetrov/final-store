package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.*;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.entities.ProductVariant;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.mappers.ProductVariantMapper;
import com.vpe.finalstore.product.repositories.ProductRepository;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import com.vpe.finalstore.product.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final ProductService productService;

    @GetMapping
    public Page<ProductSummaryDto> getProducts(
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

        List<ProductSummaryDto> dtos = productMapper.toSummaryDto(products.getContent());

        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @GetMapping("/{productId}/variants")
    public ProductWithVariantsDto getProductWithVariants(@PathVariable Integer productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        List<ProductVariant> variants = productVariantRepository.getAllByProductId(productId);
        ProductDto productDto = productMapper.toDto(product);
        List<ProductVariantDto> variantDtos = productVariantMapper.toDto(variants);

        return new ProductWithVariantsDto(productDto, variantDtos);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductCreateDto req) {
        var product = productService.createProduct(req);
        var dto = productMapper.toDto(product);

        return ResponseEntity
            .created(URI.create("/api/products/" + product.getProductId()))
            .body(dto);
    }
}
