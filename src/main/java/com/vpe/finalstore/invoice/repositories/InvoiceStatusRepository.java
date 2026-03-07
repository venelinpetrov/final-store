package com.vpe.finalstore.invoice.repositories;

import com.vpe.finalstore.invoice.entities.InvoiceStatus;
import com.vpe.finalstore.invoice.enums.InvoiceStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Integer> {
    Optional<InvoiceStatus> findByName(InvoiceStatusType name);
}

