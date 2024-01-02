package dev.magadiflo.order_service.controllers;

import dev.magadiflo.order_service.models.dtos.CustomerOrderDto;
import dev.magadiflo.order_service.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody CustomerOrderDto customerOrderDto) {
        this.orderService.createOrder(customerOrderDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
