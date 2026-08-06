package io.github.gabrielhe4.inventory_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import io.github.gabrielhe4.inventory_service.config.RabbitConfig;
import io.github.gabrielhe4.inventory_service.event.OrderPlacedEvent;
import io.github.gabrielhe4.inventory_service.service.StockService;

@Component
public class OrderPlacedListener {

    private final StockService stockService;

    public OrderPlacedListener(StockService stockService) {
        this.stockService = stockService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onOrderedPlaced(OrderPlacedEvent event) {
        for (OrderPlacedEvent.Line line: event.lines()) {
            stockService.decrement(line.productId(), line.quantity());
        }
    }

}
