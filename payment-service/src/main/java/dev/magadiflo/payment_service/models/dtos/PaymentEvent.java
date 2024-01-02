package dev.magadiflo.payment_service.models.dtos;

public record PaymentEvent(CustomerOrderDto order, String type) {
}
