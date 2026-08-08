package io.github.gabrielhe4.order_service.service;

import java.util.List;

import io.github.gabrielhe4.order_service.dto.CreateOrderRequest;
import io.github.gabrielhe4.order_service.dto.OrderResponse;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request, Long userId);

    OrderResponse findById(Long id);

    List<OrderResponse> findByUser(Long userId);

}
