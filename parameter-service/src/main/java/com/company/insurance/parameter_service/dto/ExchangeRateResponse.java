package com.company.insurance.parameter_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(String currencyCode, LocalDate rateDate, BigDecimal forexSelling) {
}
