package com.vpe.finalstore.payment.controllers;

import com.vpe.finalstore.payment.dtos.PaymentDto;
import com.vpe.finalstore.payment.dtos.PaymentIntentCreateDto;
import com.vpe.finalstore.payment.dtos.PaymentIntentResponseDto;
import com.vpe.finalstore.payment.mappers.PaymentMapper;
import com.vpe.finalstore.payment.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @Operation(
        summary = "Create PaymentIntent for cart checkout",
        description = "Creates a Stripe PaymentIntent and returns clientSecret for frontend to confirm payment"
    )
    @PostMapping("/create")
    public ResponseEntity<PaymentIntentResponseDto> createPaymentIntent(
            @Valid @RequestBody PaymentIntentCreateDto dto
    ) {
        PaymentIntentResponseDto response = paymentService.createPaymentIntentForCart(
            dto.getCartId(),
            Objects.requireNonNull(dto.getCustomerId(), "Customer ID must not be null"),
            Objects.requireNonNull(dto.getAddressId(), "Address ID must not be null")
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get payment details by ID"
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable Integer paymentId) {
        var payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentMapper.toDto(payment));
    }

    @Operation(
        summary = "Verify payment status with Stripe",
        description = "Checks the current status of a payment with Stripe by PaymentIntent ID"
    )
    @GetMapping("/verify/{paymentIntentId}")
    public ResponseEntity<String> verifyPaymentStatus(@PathVariable String paymentIntentId) {
        String status = paymentService.verifyPaymentStatus(paymentIntentId);
        return ResponseEntity.ok(status);
    }
}

