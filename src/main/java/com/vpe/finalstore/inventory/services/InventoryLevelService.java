package com.vpe.finalstore.inventory.services;

import com.vpe.finalstore.inventory.dtos.InventoryItemDto;
import com.vpe.finalstore.inventory.mappers.InventoryLevelMapper;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class InventoryLevelService {
    private final InventoryLevelRepository inventoryLevelRepository;
    private final InventoryLevelMapper inventoryLevelMapper;

    public Page<InventoryItemDto> searchInventoryLevels(
        String sku,
        String productName,
        Integer categoryId,
        Integer gte,
        Integer lte,
        Pageable pageable
    ) {
        return inventoryLevelRepository.searchInventoryLevels(sku, productName, categoryId, gte, lte, pageable)
            .map(inventoryLevelMapper::toDto);
    }
}
