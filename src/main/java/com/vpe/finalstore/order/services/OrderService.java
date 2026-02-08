package com.vpe.finalstore.order.services;

import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.cart.services.CartService;
import com.vpe.finalstore.customer.repositories.CustomerAddressRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.enums.MovementType;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import com.vpe.finalstore.order.dtos.OrderCreateDto;
import com.vpe.finalstore.order.dtos.OrderUpdateStatusDto;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import com.vpe.finalstore.order.entities.OrderItemId;
import com.vpe.finalstore.order.enums.OrderStatusType;
import com.vpe.finalstore.order.repositories.OrderRepository;
import com.vpe.finalstore.order.repositories.OrderStatusRepository;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryMovementService inventoryMovementService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderSummaryCalculator orderSummaryCalculator;

    @Transactional
    public Order createOrder(OrderCreateDto dto) {
        var customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new NotFoundException("Customer not found"));

        var address = customerAddressRepository.findById(dto.getAddressId())
            .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new BadRequestException("Address does not belong to customer");
        }

        var pendingStatus = orderStatusRepository.findByName(OrderStatusType.PENDING)
            .orElseThrow(() -> new NotFoundException("Order status PENDING not found"));

        // Create order
        var order = new Order();
        order.setCustomer(customer);
        order.setAddress(address);
        order.setStatus(pendingStatus);

        // Create order items (inventory will be deducted when order is confirmed/shipped)
        for (var itemDto : dto.getItems()) {
            var variant = variantRepository.findByVariantId(itemDto.getVariantId())
                .orElseThrow(VariantNotFoundException::new);

            // Check if variant is archived
            if (Boolean.TRUE.equals(variant.getIsArchived())) {
                throw new BadRequestException("Cannot order archived variant: " + variant.getSku());
            }

            // Create order item with denormalized data
            var orderItem = new OrderItem();
            var orderItemId = new OrderItemId();
            orderItem.setOrderItemId(orderItemId);
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setProductName(variant.getProduct().getName());
            orderItem.setSku(variant.getSku());
            orderItem.setBrandName(variant.getProduct().getBrand() != null ?
                variant.getProduct().getBrand().getName() : null);
            orderItem.setUnitPrice(variant.getUnitPrice());

            order.getOrderItems().add(orderItem);
        }

        // Calculates and sets the order summary (subtotal, tax, shipping, total)
        orderSummaryCalculator.calculateOrderSummary(order);

        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderFromCart(UUID cartId, Integer customerId, Integer addressId) {
        var cart = cartRepository.getCartWithItems(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found"));

        if (cart.isEmpty()) {
            throw new BadRequestException("Cannot create order from empty cart");
        }

        var customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Customer not found"));

        var address = customerAddressRepository.findById(addressId)
            .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new BadRequestException("Address does not belong to customer");
        }

        var pendingStatus = orderStatusRepository.findByName(OrderStatusType.PENDING)
            .orElseThrow(() -> new NotFoundException("Order status PENDING not found"));

        // Create order with PENDING status
        var order = new Order();
        order.setCustomer(customer);
        order.setAddress(address);
        order.setStatus(pendingStatus);

        // Convert cart items to order items
        for (var cartItem : cart.getCartItems()) {
            var variant = cartItem.getVariant();

            if (Boolean.TRUE.equals(variant.getIsArchived())) {
                throw new BadRequestException("Cannot order archived variant: " + variant.getSku());
            }

            // Create order item with denormalized data
            var orderItem = new OrderItem();
            var orderItemId = new OrderItemId();
            orderItem.setOrderItemId(orderItemId);
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity((int) cartItem.getQuantity());
            orderItem.setProductName(variant.getProduct().getName());
            orderItem.setSku(variant.getSku());
            orderItem.setBrandName(variant.getProduct().getBrand() != null ?
                variant.getProduct().getBrand().getName() : null);
            orderItem.setUnitPrice(variant.getUnitPrice());

            order.getOrderItems().add(orderItem);
        }

        // Calculate order summary (subtotal, tax, shipping, total)
        orderSummaryCalculator.calculateOrderSummary(order);

        order = orderRepository.save(order);

        cartService.clearCart(cartId);

        return order;
    }

    public Order getOrderById(Integer orderId) {
        return orderRepository.findOrderWithDetails(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public Page<Order> getOrdersByCustomer(Integer customerId, Pageable pageable) {
        return orderRepository.findByCustomerCustomerId(customerId, pageable);
    }

    @Transactional
    public Order updateOrderStatus(Integer orderId, OrderUpdateStatusDto dto) {
        var order = orderRepository.findOrderWithDetails(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        var currentStatus = order.getStatus().getName();
        var newStatus = dto.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        var newStatusEntity = orderStatusRepository.findByName(newStatus)
            .orElseThrow(() -> new NotFoundException("Order status not found: " + newStatus));

        handleInventoryForStatusChange(order, currentStatus, newStatus);

        order.setStatus(newStatusEntity);
        return orderRepository.save(order);
    }

    private void validateStatusTransition(OrderStatusType currentStatus, OrderStatusType newStatus) {
        var validTransitions = switch (currentStatus) {
            case PENDING -> List.of(OrderStatusType.SHIPPED, OrderStatusType.CANCELED);
            case SHIPPED -> List.of(OrderStatusType.DELIVERED, OrderStatusType.RETURNED);
            case DELIVERED -> List.of(OrderStatusType.RETURNED);
            case CANCELED, RETURNED -> List.of(); // Terminal states
        };

        if (!validTransitions.contains(newStatus)) {
            throw new BadRequestException(
                String.format("Cannot transition from %s to %s", currentStatus, newStatus)
            );
        }
    }

    private void handleInventoryForStatusChange(Order order, OrderStatusType oldStatus, OrderStatusType newStatus) {
        // Deduct inventory when order is shipped
        if (newStatus == OrderStatusType.SHIPPED) {
            for (var orderItem : order.getOrderItems()) {
                var movement = new InventoryMovementCreateDto();
                movement.setVariantId(orderItem.getVariant().getVariantId());
                movement.setMovementType(MovementType.OUT);
                movement.setQuantity(orderItem.getQuantity());
                movement.setReason("Order #" + order.getOrderId() + " shipped");

                inventoryMovementService.createMovement(movement);
            }
        }

        // Restore inventory when order is canceled or returned (if it was shipped)
        if ((newStatus == OrderStatusType.CANCELED && oldStatus == OrderStatusType.SHIPPED) ||
            newStatus == OrderStatusType.RETURNED) {
            for (var orderItem : order.getOrderItems()) {
                var movement = new InventoryMovementCreateDto();
                movement.setVariantId(orderItem.getVariant().getVariantId());
                movement.setMovementType(MovementType.IN);
                movement.setQuantity(orderItem.getQuantity());
                movement.setReason("Order #" + order.getOrderId() + " " + newStatus.name().toLowerCase());

                inventoryMovementService.createMovement(movement);
            }
        }
    }

    @Transactional
    public Order cancelOrder(Integer orderId) {
        var order = orderRepository.findOrderWithDetails(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        var currentStatus = order.getStatus().getName();

        // Only PENDING orders can be canceled by customers
        if (currentStatus != OrderStatusType.PENDING) {
            throw new BadRequestException(
                "Only pending orders can be canceled. Current status: " + currentStatus
            );
        }

        var canceledStatus = orderStatusRepository.findByName(OrderStatusType.CANCELED)
            .orElseThrow(() -> new NotFoundException("Order status CANCELED not found"));

        order.setStatus(canceledStatus);
        return orderRepository.save(order);
    }
}
