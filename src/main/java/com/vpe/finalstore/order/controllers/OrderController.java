package com.vpe.finalstore.order.controllers;

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

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(
        summary = "Get order by ID"
    )
    @PreAuthorize("@orderSecurity.isOwner(#orderId, authentication)")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
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
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId, pageable));
    }

    @Operation(
        summary = "Update order status"
    )
    @PreAuthorize("hasAuthority(T(com.vpe.finalstore.users.enums.RoleEnum).ADMIN.authority())")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
        @PathVariable Integer orderId,
        @Valid @RequestBody OrderUpdateStatusDto dto
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, dto));
    }

    @Operation(
        summary = "Cancel order"
    )
    @PreAuthorize("@orderSecurity.isOwner(#orderId, authentication)")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }
}
