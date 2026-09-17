package com.company.insurance.parameter_service.repository;

import com.company.insurance.parameter_service.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
