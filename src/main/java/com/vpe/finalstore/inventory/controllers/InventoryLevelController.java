package com.vpe.finalstore.inventory.controllers;

import com.vpe.finalstore.inventory.dtos.InventoryItemDto;
import com.vpe.finalstore.inventory.mappers.InventoryLevelMapper;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import com.vpe.finalstore.inventory.services.InventoryLevelService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/inventory")
public class InventoryLevelController {
    private final InventoryLevelMapper inventoryLevelMapper;
    private final InventoryLevelService inventoryLevelService;

    @GetMapping("/levels")
    public Page<InventoryItemDto> getInventoryLevels(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Integer gte,
        @RequestParam(required = false) Integer lte
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var inventoryLevelsPage = inventoryLevelService.getInventoryLevels(gte, lte, pageable);
        List<InventoryItemDto> dtos = inventoryLevelMapper.toDto(inventoryLevelsPage.getContent());

        return new PageImpl<>(dtos, pageable, inventoryLevelsPage.getTotalElements());
    }
}
