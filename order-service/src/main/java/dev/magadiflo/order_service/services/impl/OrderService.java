package dev.magadiflo.order_service.services.impl;

import dev.magadiflo.order_service.models.dtos.CustomerOrderDto;
import dev.magadiflo.order_service.models.dtos.OrderEvent;
import dev.magadiflo.order_service.models.entities.Order;
import dev.magadiflo.order_service.repositories.IOrderRepository;
import dev.magadiflo.order_service.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Override
    public void createOrder(CustomerOrderDto customerOrderDto) {
        Order orderToSave = Order.builder()
                .item(customerOrderDto.item())
                .quantity(customerOrderDto.quantity())
                .amount(customerOrderDto.amount())
                .status("CREATED")
                .build();
        try {
            Order orderDB = this.orderRepository.save(orderToSave);
            CustomerOrderDto customerOrder = new CustomerOrderDto(orderDB.getItem(), orderDB.getQuantity(),
                    orderDB.getAmount(), customerOrderDto.paymentMethod(), orderDB.getId(), customerOrderDto.address());

            // Send message to new-order-topic topic
            OrderEvent orderEvent = new OrderEvent(customerOrder, "ORDER_CREATED");
            this.kafkaTemplate.send("new-order-topic", orderEvent);
        } catch (Exception e) {
            orderToSave.setStatus("FAILED");
            this.orderRepository.save(orderToSave);
        }
    }
}
