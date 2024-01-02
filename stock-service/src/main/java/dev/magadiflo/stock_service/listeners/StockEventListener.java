package dev.magadiflo.stock_service.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.stock_service.models.dtos.CustomerOrderDto;
import dev.magadiflo.stock_service.models.dtos.DeliveryEvent;
import dev.magadiflo.stock_service.models.dtos.PaymentEvent;
import dev.magadiflo.stock_service.models.entities.Warehouse;
import dev.magadiflo.stock_service.repositories.IStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockEventListener {

    private final IStockRepository stockRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "new-payments-topic", groupId = "payments-group")
    public void updateStock(String event) throws JsonProcessingException {
        log.info("Update stock for order: {}", event);

        PaymentEvent paymentEvent = this.objectMapper.readValue(event, PaymentEvent.class);
        CustomerOrderDto order = paymentEvent.order();

        try {
            Iterable<Warehouse> items = this.stockRepository.findByItem(order.item());
            boolean exists = items.iterator().hasNext();

            if (!exists) {
                log.error("Stock no existe por lo que debe revertir la orden");
                throw new Exception("Stock no disponible");
            }

            items.forEach(warehouseDB -> {
                int newQuantity = warehouseDB.getQuantity() - order.quantity();
                if (newQuantity < 0) {
                    throw new RuntimeException("Stock no disponible");
                }
                warehouseDB.setQuantity(newQuantity);
                this.stockRepository.save(warehouseDB);
            });

            DeliveryEvent deliveryEvent = new DeliveryEvent(order, "STOCK_UPDATED");
            String deliveryEventJson = this.objectMapper.writeValueAsString(deliveryEvent);
            this.kafkaTemplate.send("new-stock-topic", deliveryEventJson);

        } catch (Exception e) {
            PaymentEvent paymentEventReversed = new PaymentEvent(order, "PAYMENT_REVERSED");
            String paymentEventReversedJson = this.objectMapper.writeValueAsString(paymentEventReversed);
            this.kafkaTemplate.send("reversed-payments-topic", paymentEventReversedJson);
        }

    }


}
