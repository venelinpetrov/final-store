package com.vpe.finalstore.inventory.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.entities.InventoryMovement;
import com.vpe.finalstore.inventory.repositories.InventoryMovementRepository;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class InventoryMovementService {
    private final ProductVariantRepository variantRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public Page<InventoryMovement> getMovements(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if (from != null && to != null) {
            return inventoryMovementRepository.findByCreatedAtBetween(from, to, pageable);
        } else if (from != null) {
            return inventoryMovementRepository.findByCreatedAtAfter(from, pageable);
        } else if (to != null) {
            return inventoryMovementRepository.findByCreatedAtBefore(to, pageable);
        } else {
            return inventoryMovementRepository.findAll(pageable);
        }
    }

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
