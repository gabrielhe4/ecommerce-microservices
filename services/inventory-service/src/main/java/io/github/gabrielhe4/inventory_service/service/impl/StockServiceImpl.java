package io.github.gabrielhe4.inventory_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gabrielhe4.inventory_service.dto.StockResponse;
import io.github.gabrielhe4.inventory_service.model.Stock;
import io.github.gabrielhe4.inventory_service.repository.StockRepository;
import io.github.gabrielhe4.inventory_service.service.StockService;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public void decrement(Long productId, Integer quantity) {
        log.info("Decrementing stock for product ID: {} by {}", productId, quantity);
        Stock stock = stockRepository
            .findById(productId)
            .orElseGet(() -> new Stock(productId, quantity));
        
        int newQty = stock.getQuantity() - quantity;
        stock.setQuantity(Math.max(newQty, 0)); // avoid negative
        stockRepository.save(stock);
    }

    @Override
    public Page<StockResponse> findAll(Pageable pageable) {
        log.info("Fetching all stocks");
        Page<Stock> stock = stockRepository.findAll(pageable);
        return stock.map(StockResponse::from); 
    }

    @Override
    public StockResponse findById(Long productId) {
        log.info("Fetching stock for product ID: {}", productId);
        Stock stock = stockRepository.findById(productId)
            .orElse(new Stock(productId, 0));

        return StockResponse.from(stock);
    }

    @Override
    public StockResponse setStock(Stock stock) {
        log.info("Setting stock for product ID: {}", stock.getProductId());
        return StockResponse.from(stockRepository.save(stock));
    }
    

}
