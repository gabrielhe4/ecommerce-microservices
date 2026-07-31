package io.github.gabrielhe4.order_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull Long userId,
    @NotEmpty @Valid List<OrderItemRequest> items
) {

}
