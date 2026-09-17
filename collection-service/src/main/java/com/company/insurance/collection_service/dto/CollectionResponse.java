package com.company.insurance.collection_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CollectionResponse(Long id, Long applicationId, BigDecimal totalAmount, BigDecimal collectedAmount,
                                 String currencyCode, boolean isClosed, LocalDateTime openedAt, LocalDateTime closedAt,
                                 boolean isDeleted) {
}
