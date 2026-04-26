package com.vpe.finalstore.discount.controllers;

import com.vpe.finalstore.discount.dtos.AppliedDiscountDto;
import com.vpe.finalstore.discount.dtos.DiscountCreateDto;
import com.vpe.finalstore.discount.dtos.DiscountDto;
import com.vpe.finalstore.discount.services.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    @Operation(
        summary = "Get all discounts with optional filters"
    )
    @GetMapping
    public ResponseEntity<Page<DiscountDto>> getAllDiscounts(
        @RequestParam(defaultValue = "true") Boolean isActive,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) LocalDateTime validFrom,
        @RequestParam(required = false) LocalDateTime validUntil
    ) {
        var pageable = PageRequest.of(page, size);
        var discounts = discountService.getDiscounts(isActive, validFrom, validUntil, pageable);
        return ResponseEntity.ok(discounts);
    }

    @Operation(
        summary = "Gett applied discounts"
    )
    @GetMapping("/applied")
    public ResponseEntity<Page<AppliedDiscountDto>> getAppliedDiscounts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size);
        var discounts = discountService.getAppliedDiscounts(pageable);
        return ResponseEntity.ok(discounts);
    }

    @Operation(
        summary = "Create a new discount"
    )
    @PostMapping
    public ResponseEntity<DiscountDto> createDiscount(@Valid @RequestBody DiscountCreateDto body) {
        var discountDto = discountService.createDiscount(body);

        return ResponseEntity
            .created(URI.create("/api/discounts/" + discountDto.getDiscountId()))
            .body(discountDto);
    }
}
