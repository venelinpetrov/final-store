package com.vpe.finalstore.invoice.services;

import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.invoice.entities.Invoice;
import com.vpe.finalstore.invoice.enums.InvoiceStatusType;
import com.vpe.finalstore.invoice.repositories.InvoiceRepository;
import com.vpe.finalstore.invoice.repositories.InvoiceStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceStatusRepository invoiceStatusRepository;

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

    @Transactional
    public Invoice voidInvoice(Integer invoiceId) {
        var invoice = invoiceRepository.findInvoiceWithDetails(invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice not found"));

        var currentStatus = invoice.getStatus().getName();

        if (currentStatus == InvoiceStatusType.PAID) {
            throw new BadRequestException(
                "Cannot void a paid invoice. Please process a refund first."
            );
        }

        if (currentStatus == InvoiceStatusType.VOID) {
            throw new BadRequestException("Invoice is already voided");
        }

        if (currentStatus == InvoiceStatusType.REFUNDED) {
            throw new BadRequestException("Cannot void a refunded invoice");
        }

        var voidStatus = invoiceStatusRepository.findByName(InvoiceStatusType.VOID)
            .orElseThrow(() -> new NotFoundException("Invoice status VOID not found"));

        invoice.setStatus(voidStatus);
        return invoiceRepository.save(invoice);
    }
}

