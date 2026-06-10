package com.vpe.finalstore.invoice.services;

import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.invoice.dtos.InvoiceDto;
import com.vpe.finalstore.invoice.entities.Invoice;
import com.vpe.finalstore.invoice.enums.InvoiceStatusType;
import com.vpe.finalstore.invoice.mappers.InvoiceMapper;
import com.vpe.finalstore.invoice.repositories.InvoiceRepository;
import com.vpe.finalstore.invoice.repositories.InvoiceStatusRepository;
import com.vpe.finalstore.tax.utils.TaxBreakdownMapper;
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
    private final InvoiceMapper invoiceMapper;
    private final TaxBreakdownMapper taxBreakdownMapper;

    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Integer invoiceId) {
        var invoice = invoiceRepository.findInvoiceWithDetails(invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice not found"));
        return toDto(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByCustomer(Integer customerId, Pageable pageable) {
        return invoiceRepository.findByCustomerCustomerId(customerId, pageable)
            .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceByOrderId(Integer orderId) {
        var invoice = invoiceRepository.findByOrderId(orderId)
            .orElseThrow(() -> new NotFoundException("Invoice not found for order"));
        return toDto(invoice);
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

    private InvoiceDto toDto(Invoice invoice) {
        var dto = invoiceMapper.toDto(invoice);
        taxBreakdownMapper.populateTaxBreakdown(dto, invoice);
        return dto;
    }
}

