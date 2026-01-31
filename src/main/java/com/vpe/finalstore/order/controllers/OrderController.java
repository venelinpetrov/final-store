package com.vpe.finalstore.order.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.dtos.OrderCreateDto;
import com.vpe.finalstore.order.dtos.OrderDto;
import com.vpe.finalstore.order.dtos.OrderFromCartDto;
import com.vpe.finalstore.order.dtos.OrderUpdateStatusDto;
import com.vpe.finalstore.order.mappers.OrderMapper;
import com.vpe.finalstore.order.services.OrderService;
import com.vpe.finalstore.users.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Operation(
        summary = "Create a new order"
    )
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
        @Valid @RequestBody OrderCreateDto dto,
        UriComponentsBuilder uriBuilder
    ) {
        var order = orderService.createOrder(dto);
        var orderDto = orderMapper.toDto(order);
        var uri = uriBuilder.path("/api/orders/{orderId}")
            .buildAndExpand(order.getOrderId())
            .toUri();

        return ResponseEntity.created(uri).body(orderDto);
    }

    @Operation(
        summary = "Create order from cart"
    )
    @PostMapping("/from-cart/{cartId}")
    public ResponseEntity<OrderDto> createOrderFromCart(
        @PathVariable UUID cartId,
        @Valid @RequestBody OrderFromCartDto dto,
        Authentication authentication,
        UriComponentsBuilder uriBuilder
    ) {
        // Get customer ID from authenticated user
        Integer userId = (Integer) authentication.getPrincipal();
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        Integer customerId = user.getCustomer().getCustomerId();

        var order = orderService.createOrderFromCart(cartId, customerId, dto.getAddressId());
        var orderDto = orderMapper.toDto(order);
        var uri = uriBuilder.path("/api/orders/{orderId}")
            .buildAndExpand(order.getOrderId())
            .toUri();

        return ResponseEntity.created(uri).body(orderDto);
    }

    @Operation(
        summary = "Get order by ID"
    )
    @PreAuthorize("@orderSecurity.canViewOrder(#orderId, authentication)")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Integer orderId) {
        var order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderMapper.toDto(order));
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
        return ResponseEntity.ok(orders.map(orderMapper::toDto));
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
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    @Operation(
        summary = "Cancel order"
    )
    @PreAuthorize("@orderSecurity.canCancelOrder(#orderId, authentication)")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Integer orderId) {
        var order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }
}
