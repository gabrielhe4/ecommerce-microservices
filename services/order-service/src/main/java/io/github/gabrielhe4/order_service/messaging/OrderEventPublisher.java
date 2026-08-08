package io.github.gabrielhe4.order_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import io.github.gabrielhe4.order_service.config.RabbitConfig;
import io.github.gabrielhe4.order_service.event.OrderPlacedEvent;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderPlacedEvent(OrderPlacedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.EXCHANGE,
            RabbitConfig.ROUTING_KEY, 
            event
        );
    }
}
