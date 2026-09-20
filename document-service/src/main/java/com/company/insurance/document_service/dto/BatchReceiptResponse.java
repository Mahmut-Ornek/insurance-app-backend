package com.company.insurance.document_service.dto;

import java.util.List;

public record BatchReceiptResponse(
        int totalProcessed,
        int successCount,
        int failedCount,
        List<Long> failedPaymentIds,
        String message
) {}