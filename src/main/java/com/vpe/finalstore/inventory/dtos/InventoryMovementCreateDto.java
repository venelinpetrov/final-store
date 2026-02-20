package com.vpe.finalstore.inventory.dtos;

import com.vpe.finalstore.inventory.enums.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryMovementCreateDto {
    @NotNull
    private Integer variantId;

    @NotNull
    private MovementType movementType;

    @Positive
    private Integer quantity;

    @NotBlank
    private String reason;
}
