package com.company.insurance.report_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WeeklyInsuranceReportResponse(LocalDate reportStartDate,
                                            LocalDate reportEndDate,
                                            BigDecimal totalCollectedPremium,
                                            Long totalPoliciesSold,
                                            List<ProductBreakdownDto> productBreakdown,
                                            boolean isComplete,
                                            List<String> warnings) {
}
