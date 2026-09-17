package com.company.insurance.application_service.dto;

public record PriceCalculationRequest(Long customerId,
                                      Long productId,
                                      Integer month,
                                      Integer year) {
}
