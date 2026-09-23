package com.company.insurance.policy_service.repository;

import com.company.insurance.policy_service.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByApplicationId(Long applicationId);
}
