package com.vpe.finalstore.invoice.controllers;

import com.vpe.finalstore.invoice.dtos.InvoiceDto;
import com.vpe.finalstore.invoice.mappers.InvoiceMapper;
import com.vpe.finalstore.invoice.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    @Operation(
        summary = "Get invoice by ID",
        description = "Retrieve detailed information about a specific invoice. Only the invoice owner can access it."
    )
    @PreAuthorize("@invoiceSecurity.isOwner(#invoiceId, authentication)")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable Integer invoiceId) {
        var invoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }

    @Operation(
        summary = "Get all invoices for a customer",
        description = "Retrieve a paginated list of all invoices for a specific customer. Only the customer can access their own invoices."
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Page<InvoiceDto>> getCustomerInvoices(
        @PathVariable Integer customerId,
        @PageableDefault(size = 20, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var invoices = invoiceService.getInvoicesByCustomer(customerId, pageable);
        return ResponseEntity.ok(invoices.map(invoiceMapper::toDto));
    }

    @Operation(
        summary = "Get invoice by order ID",
        description = "Retrieve the invoice associated with a specific order. Only the order owner can access it."
    )
    @PreAuthorize("@orderSecurity.canViewOrder(#orderId, authentication)")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<InvoiceDto> getInvoiceByOrder(@PathVariable Integer orderId) {
        var invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoiceMapper.toDto(invoice));
    }
}

