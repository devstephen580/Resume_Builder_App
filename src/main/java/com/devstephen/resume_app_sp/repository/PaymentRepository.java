package com.devstephen.resume_app_sp.repository;

import com.devstephen.resume_app_sp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByPaystackOrderId(String orderId);

    Optional<Payment> findByPaystackAccessCode(String paymentId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Payment> findByStatus(String status);

}
