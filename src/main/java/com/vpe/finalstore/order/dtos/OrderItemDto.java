package com.vpe.finalstore.order.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto {
    private Integer variantId;
    private short quantity;
    private String productName;
    private String sku;
    private String brandName;
    private BigDecimal unitPrice;
}
