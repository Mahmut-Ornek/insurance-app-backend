package com.company.insurance.payment_service.dto;

public record PaymentInitResponse(String token, String paymentPageUrl, String conversationId) {
}
