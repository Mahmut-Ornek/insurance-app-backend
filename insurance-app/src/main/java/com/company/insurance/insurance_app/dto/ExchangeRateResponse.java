package com.company.insurance.insurance_app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(String currencyCode, LocalDate rateDate, BigDecimal forexSelling) {
}
