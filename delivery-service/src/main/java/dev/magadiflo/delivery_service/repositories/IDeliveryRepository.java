package dev.magadiflo.delivery_service.repositories;

import dev.magadiflo.delivery_service.models.entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDeliveryRepository extends JpaRepository<Delivery, Long> {
}
