package dev.magadiflo.payment_service.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class PaymentEventListener {

    private final IPaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplateOrder;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "new-order-topic", groupId = "orders-group")
    public void processPayment(String event) throws JsonProcessingException {
        log.info("Received event for payment: {}", event);

        OrderEvent orderEvent = this.objectMapper.readValue(event, OrderEvent.class);
        CustomerOrderDto order = orderEvent.order();
        Payment payment = new Payment();

        try {
            payment.setAmount(order.amount());
            payment.setMode(order.paymentMethod());
            payment.setOrderId(order.orderId());
            payment.setStatus("SUCCESS");

            this.paymentRepository.save(payment);

            // Send payment to topic kafka
            PaymentEvent paymentEvent = new PaymentEvent(orderEvent.order(), "PAYMENT_CREATED");
            String paymentEventJsonString = this.objectMapper.writeValueAsString(paymentEvent);
            this.kafkaTemplateOrder.send("new-payments-topic", paymentEventJsonString);
        } catch (Exception e) {
            payment.setOrderId(order.orderId());
            payment.setStatus("FAILED");

            this.paymentRepository.save(payment);

            // Revertir transacción
            OrderEvent orderEventReverse = new OrderEvent(order, "ORDER_REVERSED");
            String orderEventReverseJson = this.objectMapper.writeValueAsString(orderEventReverse);
            this.kafkaTemplateOrder.send("reversed-orders-topic", orderEventReverseJson);
        }
    }

}
