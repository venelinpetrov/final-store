package com.vpe.finalstore.order.services;

import com.vpe.finalstore.customer.repositories.CustomerAddressRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.enums.MovementType;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import com.vpe.finalstore.order.dtos.OrderCreateDto;
import com.vpe.finalstore.order.dtos.OrderUpdateStatusDto;
import com.vpe.finalstore.order.dtos.OrderDto;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import com.vpe.finalstore.order.entities.OrderItemId;
import com.vpe.finalstore.order.enums.OrderStatusType;
import com.vpe.finalstore.order.mappers.OrderMapper;
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

@AllArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryMovementService inventoryMovementService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto createOrder(OrderCreateDto dto) {
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

        order = orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    public OrderDto getOrderById(Integer orderId) {
        var order = orderRepository.findOrderWithDetails(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        return orderMapper.toDto(order);
    }

    public Page<OrderDto> getOrdersByCustomer(Integer customerId, Pageable pageable) {
        var orders = orderRepository.findByCustomerCustomerId(customerId, pageable);
        return orders.map(orderMapper::toDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(Integer orderId, OrderUpdateStatusDto dto) {
        var order = orderRepository.findOrderWithDetails(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        var currentStatus = order.getStatus().getName();
        var newStatus = dto.getStatus();

        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);

        // Get new status entity
        var newStatusEntity = orderStatusRepository.findByName(newStatus)
            .orElseThrow(() -> new NotFoundException("Order status not found: " + newStatus));

        // Handle inventory based on status change
        handleInventoryForStatusChange(order, currentStatus, newStatus);

        // Update status
        order.setStatus(newStatusEntity);
        order = orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    private void validateStatusTransition(OrderStatusType currentStatus, OrderStatusType newStatus) {
        // Define valid transitions
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
}
