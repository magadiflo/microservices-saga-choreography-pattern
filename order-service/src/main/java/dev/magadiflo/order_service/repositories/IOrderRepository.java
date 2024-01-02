package dev.magadiflo.order_service.repositories;

import dev.magadiflo.order_service.models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<Order, Long> {
}
