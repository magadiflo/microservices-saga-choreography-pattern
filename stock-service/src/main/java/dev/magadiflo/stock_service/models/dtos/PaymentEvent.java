package dev.magadiflo.stock_service.models.dtos;

public record PaymentEvent(CustomerOrderDto order, String type) {
}
