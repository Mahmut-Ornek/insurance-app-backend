package com.company.insurance.report_service.service;

import com.company.insurance.report_service.client.CollectionServiceClient;
import com.company.insurance.report_service.client.PolicyServiceClient;
import com.company.insurance.report_service.client.ProductServiceClient;
import com.company.insurance.report_service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeeklyReportService {
    private static final Logger log = LoggerFactory.getLogger(WeeklyReportService.class);

    private final CollectionServiceClient collectionClient;
    private final PolicyServiceClient policyClient;
    private final ProductServiceClient productClient;

    public WeeklyReportService(CollectionServiceClient collectionClient, PolicyServiceClient policyClient,
                               ProductServiceClient productClient){
        this.collectionClient = collectionClient;
        this.policyClient = policyClient;
        this.productClient = productClient;
    }

    public WeeklyInsuranceReportResponse generateWeeklyReport() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<String> warnings = new ArrayList<>();
        boolean isComplete = true;

        // 1. Gerçekleşen toplam tahsilatı çek
        BigDecimal totalCollected = BigDecimal.ZERO;
        try {
            CollectionPeriodSummaryDto summary = collectionClient.getSummary(startDateTime, endDateTime);
            if (summary != null && summary.totalCollectedAmount() != null) {
                totalCollected = summary.totalCollectedAmount();
            } else {
                warnings.add("Tahsilat verisi alınamadı (Collection-Service boş yanıt döndü).");
                isComplete = false;
            }
        } catch (Exception ex) {
            log.error("Collection-Service çağrısında hata: {}", ex.getMessage());
            warnings.add("Tahsilat verisi alınamadı (Collection-Service erişim hatası).");
            isComplete = false;
        }

        // 2. Poliçe satış adetlerini ürün bazında çek
        List<ProductSalesCountDto> salesCounts = Collections.emptyList();
        try {
            List<ProductSalesCountDto> policySummary = policyClient.getSummary(startDate, endDate);
            if (policySummary != null) {
                salesCounts = policySummary;
            } else {
                warnings.add("Poliçe satış verisi alınamadı (Policy-Service boş yanıt döndü).");
                isComplete = false;
            }
        } catch (Exception ex) {
            log.error("Policy-Service çağrısında hata: {}", ex.getMessage());
            warnings.add("Poliçe satış verisi alınamadı (Policy-Service erişim hatası).");
            isComplete = false;
        }

        // 3. Ürün isimlerini almak için Product-Service'ten katalog listesini çek
        Map<Long, String> productNames = Collections.emptyMap();
        try {
            List<ProductDto> products = productClient.getAllProducts();
            if (products != null) {
                productNames = products.stream()
                        .collect(Collectors.toMap(ProductDto::productId, ProductDto::name, (k1, k2) -> k1));
            } else {
                warnings.add("Ürün katalog isimleri eşleştirilemedi (Product-Service boş yanıt döndü).");
                isComplete = false;
            }
        } catch (Exception ex) {
            log.error("Product-Service çağrısında hata: {}", ex.getMessage());
            warnings.add("Ürün katalog isimleri eşleştirilemedi (Product-Service erişim hatası).");
            isComplete = false;
        }

        // 4. Veriyi birleştir
        long totalPolicies = 0;
        List<ProductBreakdownDto> breakdown = new ArrayList<>();

        for (ProductSalesCountDto item : salesCounts) {
            totalPolicies += item.salesCount();
            String name = productNames.getOrDefault(item.productId(), "Bilinmeyen Ürün (ID: " + item.productId() + ")");
            breakdown.add(new ProductBreakdownDto(item.productId(), name, item.salesCount()));
        }

        return new WeeklyInsuranceReportResponse(
                startDate,
                endDate,
                totalCollected,
                totalPolicies,
                breakdown,
                isComplete,
                warnings
        );
    }
}
