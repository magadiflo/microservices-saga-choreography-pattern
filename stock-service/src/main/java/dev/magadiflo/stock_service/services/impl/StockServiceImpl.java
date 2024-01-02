package dev.magadiflo.stock_service.services.impl;

import dev.magadiflo.stock_service.models.dtos.Stock;
import dev.magadiflo.stock_service.models.entities.Warehouse;
import dev.magadiflo.stock_service.repositories.IStockRepository;
import dev.magadiflo.stock_service.services.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockServiceImpl implements IStockService {

    private final IStockRepository stockRepository;

    @Override
    public void addItems(Stock stock) {
        Iterable<Warehouse> items = this.stockRepository.findByItem(stock.item());
        if (items.iterator().hasNext()) {
            items.forEach(warehouseDB -> {
                warehouseDB.setQuantity(warehouseDB.getQuantity() + stock.quantity());
                this.stockRepository.save(warehouseDB);
            });
        } else {
            Warehouse warehouse = new Warehouse();
            warehouse.setItem(stock.item());
            warehouse.setQuantity(stock.quantity());
            this.stockRepository.save(warehouse);
        }
    }
}
