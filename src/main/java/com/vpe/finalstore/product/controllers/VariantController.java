package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.services.ProductVariantService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/variants")
class VariantController {
    private final ProductVariantService variantService;

    @PostMapping("/{variantId}/archive")
    public void archiveVariant(@PathVariable Integer variantId) {
        variantService.archiveVariant(variantId);
    }

    @PostMapping("/{variantId}/unarchive")
    public void unarchiveVariant(@PathVariable Integer variantId) {
        variantService.unarchiveVariant(variantId);
    }
}
