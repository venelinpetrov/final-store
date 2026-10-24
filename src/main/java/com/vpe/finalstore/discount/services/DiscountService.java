package com.vpe.finalstore.discount.services;

import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.discount.dtos.AppliedDiscountDto;
import com.vpe.finalstore.discount.dtos.DiscountCreateDto;
import com.vpe.finalstore.discount.dtos.DiscountDto;
import com.vpe.finalstore.discount.entities.AppliedDiscount;
import com.vpe.finalstore.discount.entities.Coupon;
import com.vpe.finalstore.discount.entities.Discount;
import com.vpe.finalstore.discount.enums.DiscountConditionType;
import com.vpe.finalstore.discount.mappers.DiscountMapper;
import com.vpe.finalstore.discount.repositories.AppliedDiscountRepository;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import com.vpe.finalstore.product.services.ProductService;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final ProductVariantRepository variantRepository;
    private final AppliedDiscountRepository appliedDiscountRepository;
    private final ProductService productService;

    public Page<DiscountDto> getDiscounts(
        Boolean isActive,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        Pageable pageable
    ) {
        return discountRepository
            .getDiscounts(isActive, validFrom, validUntil, pageable)
            .map(discountMapper::toDto);
    }

    public Page<AppliedDiscountDto> getAppliedDiscounts(Pageable pageable) {
        return appliedDiscountRepository.findAll(pageable).map(discountMapper::toDto);
    }

    @Transactional
    public DiscountDto createDiscount(DiscountCreateDto discountCreateDto) {
        if (discountCreateDto.getDiscountConditions() != null) {
            discountCreateDto.getDiscountConditions().stream()
                .filter(condition -> condition.getConditionType() == DiscountConditionType.VARIANT)
                .forEach(condition -> {
                    Integer variantId = condition.getIntValue();
                    if (!variantRepository.existsById(variantId)) {
                        throw new VariantNotFoundException();
                    }
                });
        }

        var discountEntity = discountMapper.toEntity(discountCreateDto);
        var savedDiscount = discountRepository.save(discountEntity);

        return discountMapper.toDto(savedDiscount);
    }

    public void trackAppliedDiscounts(Order order, Coupon coupon) {
        var discountsMap = productService.getVariantsToDiscountsMap();

        // Track item-level discounts
        var appliedDiscountsList = order.getOrderItems().stream()
            .filter(orderItem -> {
                return orderItem.getDiscountAmount() != null &&
                    orderItem.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0;
            })
            .map(orderItem -> {
                var discount = discountsMap.get(orderItem.getVariant().getVariantId());
                var appliedDiscount = new AppliedDiscount();

                appliedDiscount.setOrder(order);
                appliedDiscount.setOrderItem(orderItem);
                appliedDiscount.setDiscount(discount);
                appliedDiscount.setDiscountAmount(calculateItemDiscount(orderItem));
                appliedDiscount.setAppliedAt(LocalDateTime.now());

                return appliedDiscount;
            })
            .collect(Collectors.toList());

        // Track order-level discount (coupon or automatic)
        var orderLevelDiscount = getOrderLevelDiscount(order, coupon);
        if (orderLevelDiscount != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            var itemDiscountsTotal = appliedDiscountsList.stream()
                .map(AppliedDiscount::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            var orderDiscountAmount = order.getDiscountAmount().subtract(itemDiscountsTotal);

            if (orderDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
                var appliedDiscount = new AppliedDiscount();
                appliedDiscount.setOrder(order);
                appliedDiscount.setOrderItem(null); // Order-level discount
                appliedDiscount.setDiscount(orderLevelDiscount);
                appliedDiscount.setCoupon(coupon); // Track which coupon was used
                appliedDiscount.setDiscountAmount(orderDiscountAmount);
                appliedDiscount.setAppliedAt(LocalDateTime.now());
                appliedDiscountsList.add(appliedDiscount);
            }
        }

        appliedDiscountRepository.saveAll(appliedDiscountsList);

    }

    public BigDecimal calculateDiscount(Order order, BigDecimal subtotal, Coupon coupon) {
        List<ProductShort> items = order.getOrderItems().stream().map(orderItem -> {
            return new ProductShort(
                orderItem.getVariant().getVariantId(),
                orderItem.getQuantity(),
                orderItem.getVariant().getUnitPrice()
            );
        }).toList();

        return calculateDiscount(items, subtotal, coupon);
    }

    public BigDecimal calculateDiscount(Cart cart, BigDecimal subtotal, Coupon coupon) {
        List<ProductShort> items = cart.getCartItems().stream().map(cartItem -> {
            return new ProductShort(
                cartItem.getVariant().getVariantId(),
                cartItem.getQuantity(),
                cartItem.getVariant().getUnitPrice()
            );
        }).toList();

        return calculateDiscount(items, subtotal, coupon);
    }

    public Discount getOrderLevelDiscount(Order order, Coupon coupon) {
        if (coupon != null) {
            return coupon.getDiscount();
        }

        List<ProductShort> items = order.getOrderItems().stream().map(orderItem -> {
            return new ProductShort(
                orderItem.getOrderItemId(),
                orderItem.getQuantity(),
                orderItem.getVariant().getUnitPrice()
            );
        }).toList();

        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal itemsDiscount = calculateItemsDiscount(items);
        BigDecimal subtotalAfterItemDiscounts = subtotal.subtract(itemsDiscount);

        return discountRepository.findActiveDiscountForOrder(subtotalAfterItemDiscounts).orElse(null);
    }

    private BigDecimal calculateSubtotal(Order order) {
        return order.getOrderItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

     private BigDecimal calculateItemTotal(OrderItem item) {
        return item.getUnitPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private BigDecimal calculateDiscount(List<ProductShort> items, BigDecimal subtotal, Coupon coupon) {
        BigDecimal itemsDiscount = calculateItemsDiscount(items);
        BigDecimal subtotalAfterItemDiscounts = subtotal.subtract(itemsDiscount);
        BigDecimal orderDiscount = calculateOrderDiscount(subtotalAfterItemDiscounts, coupon);

        return itemsDiscount.add(orderDiscount);
    }

    private BigDecimal calculateItemsDiscount(List<ProductShort> items) {
        return items.stream()
            .map(this::calculateItemDiscount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemDiscount(ProductShort item) {
        var discountsMap = productService.getVariantsToDiscountsMap();
        var existingDiscount = discountsMap.get(item.id);

        if (existingDiscount == null) {
            return BigDecimal.ZERO;
        }

        var unitPrice = item.unitPrice;
        var quantity = item.quantity;

        BigDecimal discountAmount = switch (existingDiscount.getDiscountType()) {
            case PERCENTAGE -> unitPrice
                .multiply(existingDiscount.getValue())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(quantity));
            case FIXED -> {
                BigDecimal discountPerItem = existingDiscount.getValue().min(unitPrice);
                yield discountPerItem.multiply(BigDecimal.valueOf(quantity));
            }
            case BUY_X_GET_Y -> BigDecimal.ZERO;
        };

        return discountAmount;
    }

    private BigDecimal calculateItemDiscount(OrderItem orderItem) {
        var p = new ProductShort(
            orderItem.getVariant().getVariantId(),
            orderItem.getQuantity(),
            orderItem.getVariant().getUnitPrice()
        );
        return calculateItemDiscount(p);
    }

    private record ProductShort (
        Integer id,
        Integer quantity,
        BigDecimal unitPrice
    ) {}

    private BigDecimal calculateOrderDiscount(BigDecimal subtotalAfterItemDiscounts, Coupon coupon) {
        Discount discount = null;

        // Priority 1: Coupon-based discount
        if (coupon != null) {
            discount = coupon.getDiscount();
        } else {
            // Priority 2: Automatic order-level discount
            var existingDiscount = discountRepository.findActiveDiscountForOrder(subtotalAfterItemDiscounts);
            if (existingDiscount.isEmpty()) {
                return BigDecimal.ZERO;
            }
            discount = existingDiscount.get();
        }

        // Validate min order amount for coupon discounts
        if (coupon != null && discount.getMinOrderAmount() != null &&
                subtotalAfterItemDiscounts.compareTo(discount.getMinOrderAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = switch (discount.getDiscountType()) {
            case PERCENTAGE -> subtotalAfterItemDiscounts
                .multiply(discount.getValue())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            case FIXED -> discount.getValue();
            case BUY_X_GET_Y -> BigDecimal.ZERO; // TODO
        };

        return discount.getMaxDiscountAmount() != null && discount.getMaxDiscountAmount().compareTo(BigDecimal.ZERO) > 0
            ? discountAmount.min(discount.getMaxDiscountAmount())
            : discountAmount;
    }
}
