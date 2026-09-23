package com.company.insurance.document_service.dto;

public record ProcessedPaymentDto(Long collectionPaymentId,
                                  boolean emailSent) {
}
