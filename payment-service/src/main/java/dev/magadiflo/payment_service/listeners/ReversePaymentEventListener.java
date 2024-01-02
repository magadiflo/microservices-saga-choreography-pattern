package dev.magadiflo.payment_service.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.magadiflo.payment_service.models.dtos.CustomerOrderDto;
import dev.magadiflo.payment_service.models.dtos.OrderEvent;
import dev.magadiflo.payment_service.models.dtos.PaymentEvent;
import dev.magadiflo.payment_service.models.entities.Payment;
import dev.magadiflo.payment_service.repositories.IPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReversePaymentEventListener {

    private final ObjectMapper objectMapper;
    private final IPaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "reversed-payments-topic", groupId = "payments-group")
    public void reversePayment(String event) {
        log.info("Reverse payment for order: {}", event);
        try {
            PaymentEvent paymentEvent = this.objectMapper.readValue(event, PaymentEvent.class);
            CustomerOrderDto order = paymentEvent.order();

            Iterable<Payment> payments = this.paymentRepository.findByOrderId(order.orderId());
            payments.forEach(paymentDB -> {
                paymentDB.setStatus("FAILED");
                this.paymentRepository.save(paymentDB);
            });

            OrderEvent orderEvent = new OrderEvent(order, "ORDER_REVERSED");
            String orderReversedJson = this.objectMapper.writeValueAsString(orderEvent);
            this.kafkaTemplate.send("reversed-orders-topic", orderReversedJson);
        } catch (Exception e) {
            log.error("An exception occurred while reverting the payment");
        }
    }
}
