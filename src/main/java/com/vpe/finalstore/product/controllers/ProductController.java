package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.dtos.*;
import com.vpe.finalstore.product.entities.Product;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductVariantMapper productVariantMapper;
    private final ProductService productService;
    private final ProductVariantRepository productVariantRepository;

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
    public List<ProductVariantDto> getProductVariants(@PathVariable Integer productId) {
        var variants = productVariantRepository.findProductVariantsByProductProductId(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        return productVariantMapper.toDto(variants);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductCreateDto req) {
        var product = productService.createProduct(req);
        var dto = productMapper.toDto(product);

        return ResponseEntity
            .created(URI.create("/api/products/" + product.getProductId()))
            .body(dto);
    }

    @GetMapping("/by-any-tags")
    public ResponseEntity<Page<ProductDto>> getProductsByTags(@RequestParam("tags") Set<String> tagNames, Pageable pageable) {
        if (tagNames.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Page<Product> productPage = productService.getProductsByAnyTagNames(tagNames, pageable);

        Page<ProductDto> productDtoPage = productPage.map(productMapper::toDto);

        return ResponseEntity.ok(productDtoPage);
    }

    @GetMapping("/by-all-tags")
    public ResponseEntity<Page<ProductDto>> getProductsByAllTags(@RequestParam("tags") Set<String> tagNames, Pageable pageable) {
        if (tagNames.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Page<Product> productPage = productService.getProductsByAllTagNames(tagNames, pageable);

        Page<ProductDto> productDtoPage = productPage.map(productMapper::toDto);

        return ResponseEntity.ok(productDtoPage);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
        @PathVariable Integer productId,
        @Valid @RequestBody ProductUpdateDto updatedProduct
    ) {
        var product = productService.updateProduct(updatedProduct, productId);

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Integer productId) {
        productRepository.deleteById(productId);
    }
}
