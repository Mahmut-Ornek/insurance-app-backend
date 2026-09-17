package com.company.insurance.parameter_service.repository;

import com.company.insurance.parameter_service.entity.PricingFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingFactorRepository extends JpaRepository<PricingFactor, Long> {
    List<PricingFactor> findAllByFactorType(String factorType);
}
