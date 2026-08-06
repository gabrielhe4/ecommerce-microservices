package io.github.gabrielhe4.inventory_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.gabrielhe4.inventory_service.dto.StockResponse;
import io.github.gabrielhe4.inventory_service.model.Stock;
import io.github.gabrielhe4.inventory_service.service.StockService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stock")
    public Page<StockResponse> getAll(
        @PageableDefault(size = 10, page = 0, sort = "productId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        var response = stockService.findAll(pageable);
        return response;
    }

    @GetMapping("/stock/{productId}")
    public StockResponse getOne(@PathVariable Long productId) {
        var response = stockService.findById(productId);
        return response;
    }

    @PostMapping("/admin/stock")
    public StockResponse postMethodName(@RequestBody Stock request) {
        var response = stockService.setStock(request);        
        return response;
    }
    
}
