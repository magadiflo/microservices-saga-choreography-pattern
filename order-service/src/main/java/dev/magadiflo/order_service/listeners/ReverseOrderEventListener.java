package dev.magadiflo.order_service.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.order_service.models.dtos.OrderEvent;
import dev.magadiflo.order_service.models.entities.Order;
import dev.magadiflo.order_service.repositories.IOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReverseOrderEventListener {

    private IOrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "reversed-orders-topic", groupId = "orders-group")
    public void reverseOrder(String event) {
        log.info("Reverse order event: {}", event);
        try {
            OrderEvent orderEvent = this.objectMapper.readValue(event, OrderEvent.class);
            Optional<Order> orderOptional = this.orderRepository.findById(orderEvent.order().orderId());
            orderOptional.ifPresent(orderDB -> {
                orderDB.setStatus("FAILED");
                this.orderRepository.save(orderDB);
            });
        } catch (Exception e) {
            log.error("An exception occurred while reverting the order detail");
        }
    }
}

