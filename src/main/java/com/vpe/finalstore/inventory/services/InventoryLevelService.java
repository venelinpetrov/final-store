package com.vpe.finalstore.inventory.services;

import com.vpe.finalstore.inventory.entities.InventoryLevel;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class InventoryLevelService {
    private final InventoryLevelRepository inventoryLevelRepository;

    public Page<InventoryLevel> getOutOfStock(Pageable pageable) {
        return inventoryLevelRepository.findByQuantityInStock(0, pageable);
    }
}
