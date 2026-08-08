package io.github.gabrielhe4.order_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(
    @NotEmpty @Valid List<OrderItemRequest> items
) {

}
