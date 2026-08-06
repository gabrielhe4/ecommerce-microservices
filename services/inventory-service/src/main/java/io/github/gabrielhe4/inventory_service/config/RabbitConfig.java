package io.github.gabrielhe4.inventory_service.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.placed.inventory.queue";
    public static final String ROUTING_KEY = "order.placed";

    @Bean
    TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue inventoryQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding binding(Queue inventoryQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(inventoryQueue)
            .to(orderExchange)
            .with(ROUTING_KEY);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
