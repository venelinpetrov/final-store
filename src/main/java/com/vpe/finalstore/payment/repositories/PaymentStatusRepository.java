package com.vpe.finalstore.payment.repositories;

import com.vpe.finalstore.payment.entities.PaymentStatus;
import com.vpe.finalstore.payment.enums.PaymentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {
    Optional<PaymentStatus> findByName(PaymentStatusType name);
}

