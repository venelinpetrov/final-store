package com.vpe.finalstore.tax.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpe.finalstore.invoice.dtos.InvoiceDto;
import com.vpe.finalstore.invoice.entities.Invoice;
import com.vpe.finalstore.order.dtos.OrderDto;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.tax.dtos.TaxBreakdownDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class to deserialize JSON tax breakdown and populate DTOs
 */
@Slf4j
@Component
@AllArgsConstructor
public class TaxBreakdownMapper {
    private final ObjectMapper objectMapper;

    /**
     * Populate OrderDto with deserialized tax breakdown
     */
    public void populateTaxBreakdown(OrderDto dto, Order entity) {
        if (entity.getTaxBreakdown() != null && !entity.getTaxBreakdown().isEmpty()) {
            dto.setTaxBreakdown(deserialize(entity.getTaxBreakdown()));
        }
    }

    /**
     * Populate InvoiceDto with deserialized tax breakdown
     */
    public void populateTaxBreakdown(InvoiceDto dto, Invoice entity) {
        if (entity.getTaxBreakdown() != null && !entity.getTaxBreakdown().isEmpty()) {
            dto.setTaxBreakdown(deserialize(entity.getTaxBreakdown()));
        }
    }

    /**
     * Deserialize JSON string to TaxBreakdownDto
     */
    private TaxBreakdownDto deserialize(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, TaxBreakdownDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize tax breakdown: {}", e.getMessage(), e);
            return null;
        }
    }
}
