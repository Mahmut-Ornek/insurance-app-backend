package com.company.insurance.batch_service.dto;

public record ProcessedPaymentDto(Long collectionPaymentId,
                                  boolean emailSent) {
}
