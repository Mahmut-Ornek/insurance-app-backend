package com.company.insurance.report_service.controller;

import com.company.insurance.report_service.dto.WeeklyInsuranceReportResponse;
import com.company.insurance.report_service.service.WeeklyReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final WeeklyReportService reportService;

    public ReportController(WeeklyReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyInsuranceReportResponse> getWeeklyReport() {
        return ResponseEntity.ok(reportService.generateWeeklyReport());
    }
}
