package com.vpe.finalstore.invoice.security;

import com.vpe.finalstore.invoice.repositories.InvoiceRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("invoiceSecurity")
public class InvoiceSecurity {
    private final InvoiceRepository invoiceRepository;

    public InvoiceSecurity(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public boolean isOwner(Integer invoiceId, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();

        return invoiceRepository
            .findInvoiceWithDetails(invoiceId)
            .map(invoice -> invoice.getCustomer().getUser().getUserId().equals(userId))
            .orElse(false);
    }
}

