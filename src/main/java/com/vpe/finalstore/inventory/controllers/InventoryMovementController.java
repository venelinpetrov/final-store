package com.vpe.finalstore.inventory.controllers;

import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@RestController
@RequestMapping("/api/inventory-movements")
class InventoryMovementController {
    private final InventoryMovementService inventoryMovementService;

    @PostMapping
    public void createMovement(@Valid @RequestBody InventoryMovementCreateDto dto) {
        inventoryMovementService.createMovement(dto);
    }
}
