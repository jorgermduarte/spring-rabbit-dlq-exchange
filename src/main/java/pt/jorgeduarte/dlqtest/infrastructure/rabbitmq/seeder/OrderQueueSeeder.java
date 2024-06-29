package pt.jorgeduarte.dlqtest.infrastructure.rabbitmq.seeder;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pt.jorgeduarte.dlqtest.domain.order.Order;
import pt.jorgeduarte.dlqtest.domain.order.OrderStatus;

@Component
public class OrderQueueSeeder implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.order.q}")
    private String orderQueueName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendOrderMessageToQueue(Order order) throws JsonProcessingException{
        String orderJson = objectMapper.writeValueAsString(order);
        rabbitTemplate.convertAndSend(orderQueueName,order);
    }

    @Override
    public void run(String... args) throws Exception {
        for (long i = 1; i < 51; i++) {
            Order order = new Order("order-id-"+i,OrderStatus.PENDING);
            if(i > 45){
                order.setStatus(OrderStatus.FAILED);
            }
            sendOrderMessageToQueue(order);
        }
    }
}