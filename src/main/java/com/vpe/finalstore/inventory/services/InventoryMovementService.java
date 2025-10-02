package com.vpe.finalstore.inventory.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.entities.InventoryMovement;
import com.vpe.finalstore.inventory.repositories.InventoryMovementRepository;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class InventoryMovementService {
    private final ProductVariantRepository variantRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    @Transactional
    public void createMovement(InventoryMovementCreateDto dto) {
        var variant = variantRepository.findById(dto.getVariantId())
            .orElseThrow(() -> new NotFoundException("Variant not found"));

        var movement = new InventoryMovement();
        movement.setVariant(variant);
        movement.setReason(dto.getReason());
        movement.setMovementType(dto.getMovementType());
        movement.setQuantity(dto.getQuantity());

        inventoryMovementRepository.save(movement);
    }
}
