package com.company.insurance.parameter_service.repository;

import com.company.insurance.parameter_service.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findByCurrencyCodeAndRateDate(String currencyCode, LocalDate rateDate);
    Optional<ExchangeRate> findFirstByCurrencyCodeOrderByRateDateDesc(String currencyCode);
}
