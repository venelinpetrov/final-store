package com.vpe.finalstore.payment.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.mappers.OrderMapper;
import com.vpe.finalstore.payment.dtos.CompletePaymentDto;
import com.vpe.finalstore.payment.dtos.PaymentDto;
import com.vpe.finalstore.payment.dtos.PaymentIntentCreateDto;
import com.vpe.finalstore.payment.dtos.PaymentIntentResponseDto;
import com.vpe.finalstore.payment.mappers.PaymentMapper;
import com.vpe.finalstore.payment.services.PaymentService;
import com.vpe.finalstore.order.dtos.OrderDto;
import com.vpe.finalstore.users.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

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
            Objects.requireNonNull(dto.getCustomerId(), "Customer ID must not be null")
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

    @Operation(
        summary = "Complete payment and create order",
        description = "Verifies payment succeeded with Stripe, creates order from cart, creates invoice, and saves payment record"
    )
    @PostMapping("/{paymentIntentId}/complete")
    public ResponseEntity<OrderDto> completePayment(
            @PathVariable String paymentIntentId,
            @Valid @RequestBody CompletePaymentDto dto,
            @AuthenticationPrincipal @NonNull Integer userId,
            UriComponentsBuilder uriBuilder
    ) {
        // Get customer ID from authenticated user
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Integer customerId = user.getCustomer().getCustomerId();

        // Complete payment flow
        var order = paymentService.completePayment(
                paymentIntentId,
                dto.getCartId(),
                customerId,
                dto.getAddressId()
        );

        var orderDto = orderMapper.toDto(order);
        var uri = uriBuilder.path("/api/orders/{orderId}")
                .buildAndExpand(order.getOrderId())
                .toUri();

        return ResponseEntity.created(uri).body(orderDto);
    }
}

