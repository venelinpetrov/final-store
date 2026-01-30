package com.vpe.finalstore.order.controllers;

import com.vpe.finalstore.order.dtos.OrderCreateDto;
import com.vpe.finalstore.order.dtos.OrderDto;
import com.vpe.finalstore.order.dtos.OrderUpdateStatusDto;
import com.vpe.finalstore.order.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
class OrderController {
    private final OrderService orderService;

    @Operation(
        summary = "Create a new order"
    )
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
        @Valid @RequestBody OrderCreateDto dto,
        UriComponentsBuilder uriBuilder
    ) {
        var order = orderService.createOrder(dto);
        var uri = uriBuilder.path("/api/orders/{orderId}")
            .buildAndExpand(order.getOrderId())
            .toUri();

        return ResponseEntity.created(uri).body(order);
    }

    @Operation(
        summary = "Get order by ID"
    )
    @PreAuthorize("@orderSecurity.canViewOrder(#orderId, authentication)")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Integer orderId) {
        var order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @Operation(
        summary = "Get all orders for a customer"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Page<OrderDto>> getCustomerOrders(
        @PathVariable Integer customerId,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var orders = orderService.getOrdersByCustomer(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Update order status"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
        @PathVariable Integer orderId,
        @Valid @RequestBody OrderUpdateStatusDto dto
    ) {
        var order = orderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.ok(order);
    }
}
