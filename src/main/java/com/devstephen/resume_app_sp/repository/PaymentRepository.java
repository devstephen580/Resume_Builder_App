package com.devstephen.resume_app_sp.repository;

import com.devstephen.resume_app_sp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByReceipt(String reference);
}
