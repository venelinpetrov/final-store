package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.dtos.BrandCreateDto;
import com.vpe.finalstore.product.dtos.BrandDto;
import com.vpe.finalstore.product.dtos.BrandUpdateDto;
import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.mappers.BrandMapper;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.repositories.BrandRepository;
import com.vpe.finalstore.product.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @GetMapping
    public ResponseEntity<Page<BrandDto>> getAllBrands(Pageable pageable) {
        var brandPage = brandRepository.findAll(pageable);
        var brandDtoPage = brandPage.map(brandMapper::toDto);

        return ResponseEntity.ok(brandDtoPage);
    }

    @GetMapping("/{brandId}/products")
    public Page<ProductDto> getProducts(
        @PathVariable("brandId") Integer brandId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findProductsByBrandBrandIdAndIsArchivedIsFalse(brandId, pageable);
        List<ProductDto> dtos = productMapper.toDto(products.getContent());

        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody BrandCreateDto req) {
        var brand = brandRepository.save(brandMapper.toEntity(req));

        return ResponseEntity.ok(brandMapper.toDto(brand));
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<BrandDto> updateBrand(@Valid @RequestBody BrandUpdateDto req, @PathVariable Integer brandId) {
        var brand = brandRepository.findById(brandId).orElseThrow();

        brand.setName(req.getName());
        brand.setLogoUrl(req.getLogoUrl());

        brandRepository.save(brand);

        return ResponseEntity.ok(brandMapper.toDto(brand));
    }

    @DeleteMapping("/{brandId}")
    public void deleteBrand(@PathVariable Integer brandId) {
        brandRepository.deleteById(brandId);
    }
}
