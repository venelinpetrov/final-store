package com.vpe.finalstore.inventory.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryItemDto {
    private Integer inventoryId;
    private Integer variantId;
    private String sku;
    private Integer quantityInStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
