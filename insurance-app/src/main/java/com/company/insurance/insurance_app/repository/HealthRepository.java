package com.company.insurance.insurance_app.repository;

import com.company.insurance.insurance_app.entity.Health;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthRepository extends JpaRepository<Health, Long> {
    List<Health> findAllByIsDeletedFalse();
    Optional<Health> findByHealthIdAndIsDeletedFalse(Long healthId);
    Optional<Health> findFirstByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(Long customerId);
}