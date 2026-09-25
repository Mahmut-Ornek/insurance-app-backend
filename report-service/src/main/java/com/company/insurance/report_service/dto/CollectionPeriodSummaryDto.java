package com.company.insurance.report_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CollectionPeriodSummaryDto(BigDecimal totalCollectedAmount, LocalDateTime from, LocalDateTime to) {
}
