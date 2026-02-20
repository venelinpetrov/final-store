package com.vpe.finalstore.invoice.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.invoice.entities.Invoice;
import com.vpe.finalstore.invoice.repositories.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Integer invoiceId) {
        return invoiceRepository.findInvoiceWithDetails(invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice not found"));
    }

    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByCustomer(Integer customerId, Pageable pageable) {
        return invoiceRepository.findByCustomerCustomerId(customerId, pageable);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceByOrderId(Integer orderId) {
        return invoiceRepository.findByOrderId(orderId)
            .orElseThrow(() -> new NotFoundException("Invoice not found for order"));
    }
}

