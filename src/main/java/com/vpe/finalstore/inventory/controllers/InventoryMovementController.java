package com.vpe.finalstore.inventory.controllers;

import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import com.vpe.finalstore.product.dtos.InventoryMovementDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/inventory-movements")
class InventoryMovementController {
    private final InventoryMovementService inventoryMovementService;

    @Operation(
        summary = "Get inventory movements with optional date range filter"
    )
    @GetMapping
    public Page<InventoryMovementDto> getMovements(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime from,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime to,

        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return inventoryMovementService.getMovements(from, to, pageable);
    }

    @Operation(
        summary = "Create a new inventory movement"
    )
    @PostMapping
    public void createMovement(@Valid @RequestBody InventoryMovementCreateDto dto) {
        inventoryMovementService.createMovement(dto);
    }
}
