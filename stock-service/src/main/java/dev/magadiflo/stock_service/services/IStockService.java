package dev.magadiflo.stock_service.services;

import dev.magadiflo.stock_service.models.dtos.Stock;

public interface IStockService {
    void addItems(Stock stock);
}
