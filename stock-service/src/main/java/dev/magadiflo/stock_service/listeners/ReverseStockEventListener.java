package dev.magadiflo.stock_service.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ReverseStockEventListener {

    private final IStockRepository stockRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "reversed-stock-topic", groupId = "stocks-group")
    public void reverseStock(String event) {
        log.info("Reverse stock for order: {}", event);

        try {
            DeliveryEvent deliveryEvent = this.objectMapper.readValue(event, DeliveryEvent.class);
            Iterable<Warehouse> inventories = this.stockRepository.findByItem(deliveryEvent.order().item());

            inventories.forEach(warehouseDB -> {
                warehouseDB.setQuantity(warehouseDB.getQuantity() + deliveryEvent.order().quantity());
                this.stockRepository.save(warehouseDB);
            });

            PaymentEvent paymentEvent = new PaymentEvent(deliveryEvent.order(), "PAYMENT_REVERSED");
            String paymentEventJson = this.objectMapper.writeValueAsString(paymentEvent);
            this.kafkaTemplate.send("reversed-payments-topic", paymentEventJson);
        } catch (Exception e) {
            log.error("Ocurrió un error al tratar de revertir el stock");
            e.printStackTrace();
        }
    }
}
