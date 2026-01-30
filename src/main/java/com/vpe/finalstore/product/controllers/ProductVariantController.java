package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.dtos.ProductImageAssignmentDto;
import com.vpe.finalstore.product.dtos.ProductVariantDto;
import com.vpe.finalstore.product.dtos.ProductVariantUpdateDto;
import com.vpe.finalstore.product.mappers.ProductVariantMapper;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import com.vpe.finalstore.product.services.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
        summary = "Get variant by ID"
    )
    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> getVariant(@PathVariable Integer variantId) {
        var variant = variantRepository.findByVariantId(variantId)
            .orElseThrow(VariantNotFoundException::new);

        return ResponseEntity.ok(variantMapper.toDto(variant));
    }

    @Operation(
        summary = "Update a variant"
    )
    @PutMapping("/{variantId}")
    public void updateVariant(@PathVariable Integer variantId, @Valid @RequestBody ProductVariantUpdateDto req) {
        variantService.updateVariant(variantId, req);
    }

    @Operation(
        summary = "Archive a variant"
    )
    @PostMapping("/{variantId}/archive")
    public void archiveVariant(@PathVariable Integer variantId) {
        variantService.archiveVariant(variantId);
    }

    @Operation(
        summary = "Unarchive a variant"
    )
    @PostMapping("/{variantId}/unarchive")
    public void unarchiveVariant(@PathVariable Integer variantId) {
        variantService.unarchiveVariant(variantId);
    }

    @Operation(
        summary = "Delete a variant"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{variantId}")
    public void deleteVariant(@PathVariable Integer variantId) {
        variantService.deleteVariant(variantId);
    }

    @Operation(
        summary = "Assign images to a variant"
    )
    @PostMapping("/{variantId}/images")
    public ResponseEntity<Void> assignImages(@PathVariable Integer variantId, @Valid @RequestBody ProductImageAssignmentDto assignmentDto) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variantService.assignImages(assignmentDto.getImageIds(), variant);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Unassign images from a variant"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{variantId}/images")
    public void unassignImages(@PathVariable Integer variantId, @Valid @RequestBody ProductImageAssignmentDto assignmentDto) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variantService.unassignImages(assignmentDto.getImageIds(), variant);
    }
}
