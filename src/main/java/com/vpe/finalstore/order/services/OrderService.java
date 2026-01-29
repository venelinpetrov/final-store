package com.vpe.finalstore.order.services;

import com.vpe.finalstore.customer.repositories.CustomerAddressRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.dtos.OrderCreateDto;
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

@AllArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final ProductVariantRepository variantRepository;
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
}
