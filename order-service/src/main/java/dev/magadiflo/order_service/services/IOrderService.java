package dev.magadiflo.order_service.services;

import dev.magadiflo.order_service.models.dtos.CustomerOrderDto;

public interface IOrderService {
    void createOrder(CustomerOrderDto customerOrderDto);
}
