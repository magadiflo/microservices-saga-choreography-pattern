package dev.magadiflo.stock_service.models.dtos;

public record DeliveryEvent(CustomerOrderDto order, String type) {
}
