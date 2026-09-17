package com.company.insurance.product_service.dto;

import java.math.BigDecimal;

public record BasePriceResponse(Long priceId, Long productId, BigDecimal basePrice, String currencyCode,
                                Short month, Short year, boolean isDeleted, String createdBy, String updatedBy) {
}
