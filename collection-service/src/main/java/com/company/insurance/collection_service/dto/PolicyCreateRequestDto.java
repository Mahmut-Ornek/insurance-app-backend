package com.company.insurance.collection_service.dto;

import java.math.BigDecimal;

public record PolicyCreateRequestDto(Long applicationId, BigDecimal premiumAmount, String currencyCode) {}