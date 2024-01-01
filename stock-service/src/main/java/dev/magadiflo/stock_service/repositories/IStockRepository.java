package dev.magadiflo.stock_service.repositories;

import dev.magadiflo.stock_service.models.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStockRepository extends JpaRepository<Warehouse, Long> {
    Iterable<Warehouse> findByItem(String item);
}
