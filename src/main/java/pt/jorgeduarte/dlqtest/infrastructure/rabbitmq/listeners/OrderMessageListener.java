package pt.jorgeduarte.dlqtest.infrastructure.rabbitmq.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pt.jorgeduarte.dlqtest.domain.order.Order;
import pt.jorgeduarte.dlqtest.domain.order.OrderStatus;

@Component
public class OrderMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class);

    @RabbitListener(queues = "order-processing.q")
    public void receiveMessage(final Order order) {
        logger.info("Received a new order message to be processed: {}", order);

        if (OrderStatus.FAILED.equals(order.getStatus())) {
            // forcing to throw an error to move the message to the dlq
            throw new RuntimeException(String.format("Failed to process the order with orderId: %s", order.getOrderId()));
        }

        logger.info("Order processed successfully: {}", order);
    }
}
