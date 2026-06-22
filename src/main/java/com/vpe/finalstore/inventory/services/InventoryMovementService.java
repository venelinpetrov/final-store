package com.vpe.finalstore.inventory.services;

import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.entities.InventoryLevel;
import com.vpe.finalstore.inventory.entities.InventoryMovement;
import com.vpe.finalstore.inventory.enums.MovementType;
import com.vpe.finalstore.inventory.mappers.InventoryMovementMapper;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import com.vpe.finalstore.inventory.repositories.InventoryMovementRepository;
import com.vpe.finalstore.product.dtos.InventoryMovementDto;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
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
    private final InventoryLevelRepository inventoryLevelRepository;
    private final InventoryMovementMapper inventoryMovementMapper;

    public Page<InventoryMovementDto> getMovements(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<InventoryMovement> movements;
        if (from != null && to != null) {
            movements = inventoryMovementRepository.findByCreatedAtBetween(from, to, pageable);
        } else if (from != null) {
            movements = inventoryMovementRepository.findByCreatedAtAfter(from, pageable);
        } else if (to != null) {
            movements = inventoryMovementRepository.findByCreatedAtBefore(to, pageable);
        } else {
            movements = inventoryMovementRepository.findAll(pageable);
        }
        return movements.map(inventoryMovementMapper::toDto);
    }

    @Transactional
    public void createMovement(InventoryMovementCreateDto dto) {
        var variant = variantRepository.findById(dto.getVariantId())
            .orElseThrow(VariantNotFoundException::new);

        var inventoryItem = inventoryLevelRepository.findByVariantVariantIdForUpdate(variant.getVariantId())
            .orElseGet(() -> inventoryLevelRepository.save(new InventoryLevel(variant, 0)));

        var quantityInStock = inventoryItem.getQuantityInStock();

        if (dto.getMovementType() == MovementType.OUT && dto.getQuantity() > quantityInStock) {
                throw new BadRequestException(
                    String.format("Cannot move out %d items. Only %d in stock", dto.getQuantity(), quantityInStock)
                );
        }

        var movement = new InventoryMovement();
        movement.setVariant(variant);
        movement.setReason(dto.getReason());
        movement.setMovementType(dto.getMovementType());
        movement.setQuantity(dto.getQuantity());

        inventoryMovementRepository.save(movement);
    }
}
