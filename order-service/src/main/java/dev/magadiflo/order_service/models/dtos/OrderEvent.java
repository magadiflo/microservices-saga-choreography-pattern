package dev.magadiflo.order_service.models.dtos;

public record OrderEvent(CustomerOrderDto order, String type) {
}
