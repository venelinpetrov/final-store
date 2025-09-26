package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.dtos.ProductVariantDto;
import com.vpe.finalstore.product.dtos.ProductVariantUpdateDto;
import com.vpe.finalstore.product.mappers.ProductVariantMapper;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import com.vpe.finalstore.product.services.ProductVariantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/variants")
class ProductVariantController {
    private final ProductVariantService variantService;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantMapper variantMapper;

    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> getVariant(@PathVariable Integer variantId) {
        var variant = variantRepository.findByVariantId(variantId)
            .orElseThrow(() -> new NotFoundException("Variant not found"));

        return ResponseEntity.ok(variantMapper.toDto(variant));
    }

    @PutMapping("/{variantId}")
    public void updateVariant(@PathVariable Integer variantId, @Valid @RequestBody ProductVariantUpdateDto req) {
        variantService.updateVariant(variantId, req);
    }

    @PostMapping("/{variantId}/archive")
    public void archiveVariant(@PathVariable Integer variantId) {
        variantService.archiveVariant(variantId);
    }

    @PostMapping("/{variantId}/unarchive")
    public void unarchiveVariant(@PathVariable Integer variantId) {
        variantService.unarchiveVariant(variantId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{variantId}")
    public void deleteVariant(@PathVariable Integer variantId) {
        variantService.deleteVariant(variantId);
    }
}
