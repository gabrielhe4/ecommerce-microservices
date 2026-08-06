package io.github.gabrielhe4.inventory_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.gabrielhe4.inventory_service.dto.StockResponse;
import io.github.gabrielhe4.inventory_service.model.Stock;

public interface StockService {

    void decrement(Long productId, Integer quantity);

    Page<StockResponse> findAll(Pageable pageable);

    StockResponse findById(Long productId);

    StockResponse setStock(Stock stock);
    
}
