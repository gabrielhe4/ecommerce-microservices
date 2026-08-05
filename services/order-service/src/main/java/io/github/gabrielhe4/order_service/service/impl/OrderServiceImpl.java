package io.github.gabrielhe4.order_service.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gabrielhe4.order_service.client.ProductClient;
import io.github.gabrielhe4.order_service.dto.CreateOrderRequest;
import io.github.gabrielhe4.order_service.dto.OrderItemRequest;
import io.github.gabrielhe4.order_service.dto.OrderResponse;
import io.github.gabrielhe4.order_service.dto.ProductDto;
import io.github.gabrielhe4.order_service.event.OrderPlacedEvent;
import io.github.gabrielhe4.order_service.exception.ResourceNotFoundException;
import io.github.gabrielhe4.order_service.messaging.OrderEventPublisher;
import io.github.gabrielhe4.order_service.model.Order;
import io.github.gabrielhe4.order_service.model.OrderItem;
import io.github.gabrielhe4.order_service.repository.OrderRepository;
import io.github.gabrielhe4.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher eventPublisher;

    private static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        log.info("Creating a new order...");

        Order order = new Order();
        order.setUserId(request.userId());
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq: request.items()) {
            ProductDto product = productClient.getProduct(itemReq.productId());

            OrderItem item = OrderItem.builder()
                                .productId(product.id())
                                .productName(product.name())
                                .unitPrice(product.finalPrice())
                                .quantity(itemReq.quantity())
                                .build();
            order.addItem(item);

            total = total.add(product.finalPrice()
                        .multiply(BigDecimal.valueOf(itemReq.quantity())));
        }

        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        log.info("A new order was created with id: {}", savedOrder.getId());

        log.info("Publishing an order placed event to inventory service...");
        OrderPlacedEvent event = new OrderPlacedEvent(
            savedOrder.getId(),
            savedOrder.getItems().stream()
                .map(i -> new OrderPlacedEvent.Line(i.getProductId(),
                        i.getQuantity()))
                .toList()
        );
        eventPublisher.publishOrderPlacedEvent(event);

        return toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        log.info("Searching order by id: {}", id);
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        log.info("Order founded, returning values!");
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByUser(Long userId) {
        log.info("Retrieving orders by user ID: {}", userId);

        return orderRepository.findByUserId(userId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.Item> items = order.getItems().stream()
                .map(i -> new OrderResponse.Item(
                    i.getProductId(), i.getProductName(), i.getUnitPrice(), i.getQuantity()
                )).toList();
        return new OrderResponse(
            order.getId(), order.getUserId(), order.getStatus(),
            order.getTotal(), order.getCreatedAt(), items
        );
    }


}
