package com.company.insurance.batch_service.dto;

public record ReceiptProcessingItem(
        PaymentSummaryDto payment,
        String customerName,
        ReceiptCreateRequest createRequest
) {}