package com.vpe.finalstore.payment.repositories;

import com.vpe.finalstore.payment.entities.CustomerPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerPaymentMethodRepository extends JpaRepository<CustomerPaymentMethod, Integer> {
    List<CustomerPaymentMethod> findByCustomer_CustomerId(Integer customerId);

    Optional<CustomerPaymentMethod> findByCustomer_CustomerIdAndIsDefaultTrue(Integer customerId);

    Optional<CustomerPaymentMethod> findByStripeMethodId(String stripeMethodId);

    Optional<CustomerPaymentMethod> findByMethodIdAndCustomer_CustomerId(Integer methodId, Integer customerId);
}

