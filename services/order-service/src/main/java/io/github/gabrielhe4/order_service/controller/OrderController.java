package io.github.gabrielhe4.order_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.gabrielhe4.order_service.dto.CreateOrderRequest;
import io.github.gabrielhe4.order_service.dto.OrderResponse;
import io.github.gabrielhe4.order_service.service.OrderService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        var response = orderService.create(request);
        return response;
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@RequestParam Long id) {
        var response = orderService.findById(id);
        return response;
    }

    @GetMapping()
    public List<OrderResponse> getByUser(@RequestParam Long userId) {
        var response = orderService.findByUser(userId);
        return response;
    }



}
