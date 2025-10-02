package com.vpe.finalstore.inventory.services;

import com.vpe.finalstore.inventory.entities.InventoryLevel;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class InventoryLevelService {
    private final InventoryLevelRepository inventoryLevelRepository;

    public List<InventoryLevel> getOutOfStock() {
        return inventoryLevelRepository.getAllByQuantityInStock(0);
    }
}
