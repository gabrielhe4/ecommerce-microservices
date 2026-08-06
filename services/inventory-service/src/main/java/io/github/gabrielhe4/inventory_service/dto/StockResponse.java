package io.github.gabrielhe4.inventory_service.dto;

import io.github.gabrielhe4.inventory_service.model.Stock;

public record StockResponse(
    Long productId,
    Integer quantity
) {

    public static StockResponse from(Stock stock) {
        return new StockResponse(stock.getProductId(), 
            stock.getQuantity());
    }
}
