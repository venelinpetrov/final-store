package com.vpe.finalstore.invoice.repositories;

import com.vpe.finalstore.invoice.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @EntityGraph(attributePaths = {"order", "customer", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.invoiceId = :invoiceId")
    Optional<Invoice> findInvoiceWithDetails(@Param("invoiceId") Integer invoiceId);

    @EntityGraph(attributePaths = {"order", "customer", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.order.orderId = :orderId")
    Optional<Invoice> findByOrderId(@Param("orderId") Integer orderId);

    @EntityGraph(attributePaths = {"order", "customer", "payments"})
    @Query("SELECT i FROM Invoice i WHERE i.customer.customerId = :customerId")
    Page<Invoice> findByCustomerCustomerId(@Param("customerId") Integer customerId, Pageable pageable);
}

