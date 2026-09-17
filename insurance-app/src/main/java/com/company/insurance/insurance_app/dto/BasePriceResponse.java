package com.company.insurance.insurance_app.dto;

import java.math.BigDecimal;

public record BasePriceResponse(
        Long priceId,
        Long productId,
        BigDecimal basePrice,
        String currencyCode,
        Integer month,
        Integer year
) {}
