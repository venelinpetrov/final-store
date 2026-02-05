package com.vpe.finalstore.payment.repositories;

import com.vpe.finalstore.payment.entities.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @EntityGraph(attributePaths = {"invoice", "method", "status"})
    @Query("SELECT p FROM Payment p WHERE p.paymentId = :paymentId")
    Optional<Payment> findPaymentWithDetails(@Param("paymentId") Integer paymentId);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByStripeChargeId(String stripeChargeId);

    @EntityGraph(attributePaths = {"method", "status"})
    @Query("SELECT p FROM Payment p WHERE p.invoice.invoiceId = :invoiceId")
    List<Payment> findByInvoiceId(@Param("invoiceId") Integer invoiceId);
}

