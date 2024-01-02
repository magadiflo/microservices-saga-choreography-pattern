package dev.magadiflo.stock_service.models.dtos;

public record CustomerOrderDto(String item, Integer quantity, Double amount, String paymentMethod, Long orderId,
                               String address) {
}
