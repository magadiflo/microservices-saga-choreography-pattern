package dev.magadiflo.payment_service.models.dtos;

public record OrderEvent(CustomerOrderDto order, String type) {
}
