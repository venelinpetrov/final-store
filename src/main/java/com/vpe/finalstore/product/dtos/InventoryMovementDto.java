package com.vpe.finalstore.product.dtos;

import com.vpe.finalstore.inventory.enums.MovementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryMovementDto {
    private Integer movementId;
    private Integer variantId;
    private MovementType movementType;
    private short quantity;
    private String reason;
    private LocalDateTime createdAt;
}
