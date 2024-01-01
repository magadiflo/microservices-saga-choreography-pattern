package dev.magadiflo.delivery_service.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.delivery_service.models.dtos.CustomerOrderDto;
import dev.magadiflo.delivery_service.models.dtos.DeliveryEvent;
import dev.magadiflo.delivery_service.models.entities.Delivery;
import dev.magadiflo.delivery_service.repositories.IDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeliveryEventListener {

    private final IDeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "new-stock-topic", groupId = "stocks-group")
    public void deliveryOrder(String event) throws JsonProcessingException {
        log.info("Ship order for order: {}", event);

        DeliveryEvent deliveryEvent = this.objectMapper.readValue(event, DeliveryEvent.class);
        CustomerOrderDto order = deliveryEvent.order();

        try {
            if (order.address() == null) {
                throw new Exception("Address not present");
            }

            Delivery delivery = Delivery.builder()
                    .address(order.address())
                    .orderId(order.orderId())
                    .status("SUCCESS")
                    .build();

            this.deliveryRepository.save(delivery);
        } catch (Exception e) {
            Delivery delivery = Delivery.builder()
                    .orderId(order.orderId())
                    .status("FAILED")
                    .build();
            this.deliveryRepository.save(delivery);

            log.info("Order in Delivery Exception: {}", order);

            DeliveryEvent deliveryEventReverse = new DeliveryEvent(order, "STOCK_REVERSED");
            String deliveryEventReverseJson = this.objectMapper.writeValueAsString(deliveryEventReverse);
            this.kafkaTemplate.send("reversed-stock-topic", deliveryEventReverseJson);
        }
    }
}
