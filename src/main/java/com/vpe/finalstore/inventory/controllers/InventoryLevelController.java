package com.vpe.finalstore.inventory.controllers;

import com.vpe.finalstore.inventory.dtos.InventoryItemDto;
import com.vpe.finalstore.inventory.mappers.InventoryLevelMapper;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import com.vpe.finalstore.inventory.services.InventoryLevelService;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/inventory")
public class InventoryLevelController {
    private final InventoryLevelMapper inventoryLevelMapper;
    private final InventoryLevelService inventoryLevelService;
    private final InventoryLevelRepository inventoryLevelRepository;

    @Operation(
        summary = "Search inventory levels with optional filters"
    )
    @GetMapping("/levels")
    public Page<InventoryItemDto> getInventoryLevels(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Integer gte,
        @RequestParam(required = false) Integer lte,
        @RequestParam(required = false) String sku,
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) Integer categoryId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var levels = inventoryLevelService.searchInventoryLevels(
            sku, productName, categoryId, gte, lte, pageable
        );
        return levels.map(inventoryLevelMapper::toDto);
    }

    @Operation(
        summary = "Get inventory level for a specific variant"
    )
    @GetMapping("/levels/{variantId}")
    public InventoryItemDto getInventoryItem(@PathVariable Integer variantId) {
        var item = inventoryLevelRepository.findByVariantVariantId(variantId)
            .orElseThrow(VariantNotFoundException::new);

        return inventoryLevelMapper.toDto(item);
    }
}
