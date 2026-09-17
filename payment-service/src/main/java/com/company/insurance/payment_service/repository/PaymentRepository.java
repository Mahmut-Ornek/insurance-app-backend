package com.company.insurance.payment_service.repository;

import com.company.insurance.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByToken(String token);
    Optional<Payment> findByConversationId(String conversationId);
}
