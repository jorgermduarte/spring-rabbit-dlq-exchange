package pt.jorgeduarte.dlqtest.infrastructure.rabbitmq.configs;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderListenerConfig {

    @Value("${rabbitmq.queue.order.q}")
    private String queueName;
    @Value("${rabbitmq.queue.order.dlq}")
    private String dlqQueueName;

    @Autowired
    private DirectExchange globalDLQExchange;
    @Value("${rabbitmq.queue.order.dlq.r.key}")
    private String globalDLQExchangeRoutingKey;
    @Bean
    Queue orderQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", globalDLQExchange.getName())
                .withArgument("x-dead-letter-routing-key", globalDLQExchangeRoutingKey)
                .build();
    }
    @Bean
    Queue orderDlqQueue(){
        return QueueBuilder.durable(dlqQueueName)
                .build();
    }

    @Bean
    Binding dlqBinding(Queue orderDlqQueue) {
        return BindingBuilder.bind(orderDlqQueue()).to(globalDLQExchange).with(globalDLQExchangeRoutingKey);
    }
}
