package dev.magadiflo.stock_service.controllers;

import dev.magadiflo.stock_service.models.dtos.Stock;
import dev.magadiflo.stock_service.services.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/stocks")
public class StockController {

    private final IStockService stockService;

    @PostMapping
    public ResponseEntity<Void> addItems(@RequestBody Stock stock) {
        this.stockService.addItems(stock);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
