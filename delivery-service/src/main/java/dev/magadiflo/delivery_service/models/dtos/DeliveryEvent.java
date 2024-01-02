package dev.magadiflo.delivery_service.models.dtos;

public record DeliveryEvent(CustomerOrderDto order, String type) {
}