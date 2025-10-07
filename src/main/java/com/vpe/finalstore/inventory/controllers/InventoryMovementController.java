package com.vpe.finalstore.inventory.controllers;

import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.mappers.InventoryMovementMapper;
import com.vpe.finalstore.inventory.repositories.InventoryMovementRepository;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import com.vpe.finalstore.product.dtos.InventoryMovementDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/inventory-movements")
class InventoryMovementController {
    private final InventoryMovementService inventoryMovementService;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryMovementMapper inventoryMovementMapper;

    @GetMapping
    public Page<InventoryMovementDto> getMovements(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        var movements = inventoryMovementRepository.findAll(pageable);
        return movements.map(inventoryMovementMapper::toDto);
    }

    @PostMapping
    public void createMovement(@Valid @RequestBody InventoryMovementCreateDto dto) {
        inventoryMovementService.createMovement(dto);
    }
}
