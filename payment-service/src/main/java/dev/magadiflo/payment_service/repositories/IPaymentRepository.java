package dev.magadiflo.payment_service.repositories;

import dev.magadiflo.payment_service.models.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
}
