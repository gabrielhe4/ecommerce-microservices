package io.github.gabrielhe4.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.github.gabrielhe4.order_service.model.OrderStatus;

public record OrderResponse(
    Long id,
    Long userId,
    OrderStatus status,
    BigDecimal total,
    LocalDateTime createdAt,
    List<Item> items
) {
    public record Item(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity
    ) {}

}
