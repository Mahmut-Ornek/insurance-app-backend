package com.company.insurance.insurance_app.service;

import com.company.insurance.insurance_app.client.ParameterServiceClient;
import com.company.insurance.insurance_app.dto.PricingFactorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PricingFactorCacheServiceTest {

    private ParameterServiceClient parameterServiceClient;
    private PricingFactorCacheService cacheService;

    @BeforeEach
    void setUp() {
        parameterServiceClient = Mockito.mock(ParameterServiceClient.class);

        // Veritabanındaki gerçek pricing_factor tablosu verileri
        List<PricingFactorResponse> mockFactors = List.of(
                // JOB_RISK
                new PricingFactorResponse(1L, "JOB_RISK", new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1.00"), "Risk 1 Meslek"),
                new PricingFactorResponse(2L, "JOB_RISK", new BigDecimal("2"), new BigDecimal("2"), new BigDecimal("1.15"), "Risk 2 Meslek"),
                new PricingFactorResponse(3L, "JOB_RISK", new BigDecimal("3"), new BigDecimal("3"), new BigDecimal("1.30"), "Risk 3 Meslek"),
                new PricingFactorResponse(4L, "JOB_RISK", new BigDecimal("4"), new BigDecimal("4"), new BigDecimal("1.45"), "Risk 4 Meslek"),
                new PricingFactorResponse(5L, "JOB_RISK", new BigDecimal("5"), new BigDecimal("5"), new BigDecimal("1.60"), "Risk 5 Meslek"),

                // AGE
                new PricingFactorResponse(6L, "AGE", new BigDecimal("0"), new BigDecimal("29"), new BigDecimal("1.00"), "30 yaş altı"),
                new PricingFactorResponse(7L, "AGE", new BigDecimal("30"), new BigDecimal("50"), new BigDecimal("1.20"), "30-50 yaş bandı"),
                new PricingFactorResponse(8L, "AGE", new BigDecimal("51"), new BigDecimal("150"), new BigDecimal("1.50"), "50 yaş üstü"),

                // BMI
                new PricingFactorResponse(9L, "BMI", new BigDecimal("0"), new BigDecimal("24.99"), new BigDecimal("1.00"), "İdeal kilo"),
                new PricingFactorResponse(10L, "BMI", new BigDecimal("25.00"), new BigDecimal("29.99"), new BigDecimal("1.10"), "Fazla kilolu"),
                new PricingFactorResponse(11L, "BMI", new BigDecimal("30.00"), new BigDecimal("100.00"), new BigDecimal("1.25"), "Obez"),

                // Katsayılar
                new PricingFactorResponse(12L, "SMOKING", new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1.25"), "Sigara kullananlar"),
                new PricingFactorResponse(13L, "DISEASE_SEVERITY_COEFFICIENT", null, null, new BigDecimal("0.05"), "Hastalık şiddet çarpan katsayısı"),
                new PricingFactorResponse(14L, "JOB_UNEMPLOYED", null, null, new BigDecimal("1.10"), "İşsiz / Meslek bilgisi olmayan")
        );

        when(parameterServiceClient.getAllPricingFactors()).thenReturn(mockFactors);

        cacheService = new PricingFactorCacheService(parameterServiceClient);
        cacheService.init(); // Önbelleği doldur
    }

    // --- Null ve Geçersiz Giriş Testleri ---

    @Test
    @DisplayName("Değer null verildiğinde getMultiplierForRange Optional.empty dönmeli")
    void shouldReturnEmptyWhenValueIsNull() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Tanımsız bir factorType sorgulandığında Optional.empty dönmeli")
    void shouldReturnEmptyWhenFactorTypeIsUnknown() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("UNKNOWN_FACTOR", new BigDecimal("25"));
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Tanımlı aralıkların dışında bir değer sorgulandığında Optional.empty dönmeli")
    void shouldReturnEmptyWhenValueIsOutOfAnyRange() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", new BigDecimal("151"));
        assertTrue(result.isEmpty());
    }

    // --- AGE Sınır Değer Testleri (0-29: 1.00 | 30-50: 1.20 | 51-150: 1.50) ---

    @Test
    @DisplayName("Yaş tam alt sınırda (0) çarpan 1.00 dönmeli")
    void shouldReturnOneWhenAgeIsAtAbsoluteMinimum() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", new BigDecimal("0"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.00"), result.get());
    }

    @Test
    @DisplayName("Yaş 29 olduğunda (ilk basamak üst sınırı) çarpan 1.00 dönmeli")
    void shouldReturnOneWhenAgeIsAtFirstBoundaryUpperBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", new BigDecimal("29"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.00"), result.get());
    }

    @Test
    @DisplayName("Yaş 30 olduğunda (ikinci basamak alt sınırı) çarpan 1.20 dönmeli")
    void shouldReturnOnePointTwoWhenAgeIsAtSecondBoundaryLowerBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", new BigDecimal("30"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.20"), result.get());
    }

    @Test
    @DisplayName("Yaş 50 olduğunda (ikinci basamak üst sınırı) çarpan 1.20 dönmeli")
    void shouldReturnOnePointTwoWhenAgeIsAtSecondBoundaryUpperBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", new BigDecimal("50"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.20"), result.get());
    }

    @Test
    @DisplayName("Yaş 51 olduğunda (üçüncü basamak alt sınırı) çarpan 1.50 dönmeli")
    void shouldReturnOnePointFiveWhenAgeIsAtThirdBoundaryLowerBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("AGE", new BigDecimal("51"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.50"), result.get());
    }

    // --- BMI Sınır Değer Testleri (0-24.99: 1.00 | 25.00-29.99: 1.10 | 30.00-100.00: 1.25) ---

    @Test
    @DisplayName("BMI 24.99 (ideal kilo üst sınırı) için çarpan 1.00 dönmeli")
    void shouldReturnOneWhenBmiIsAtNormalUpperBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("BMI", new BigDecimal("24.99"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.00"), result.get());
    }

    @Test
    @DisplayName("BMI 25.00 (fazla kilolu alt sınırı) için çarpan 1.10 dönmeli")
    void shouldReturnOnePointOneWhenBmiIsAtOverweightLowerBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("BMI", new BigDecimal("25.00"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.10"), result.get());
    }

    @Test
    @DisplayName("BMI 29.99 (fazla kilolu üst sınırı) için çarpan 1.10 dönmeli")
    void shouldReturnOnePointOneWhenBmiIsAtOverweightUpperBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("BMI", new BigDecimal("29.99"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.10"), result.get());
    }

    @Test
    @DisplayName("BMI 30.00 (obez alt sınırı) için çarpan 1.25 dönmeli")
    void shouldReturnOnePointTwentyFiveWhenBmiIsAtObeseLowerBound() {
        Optional<BigDecimal> result = cacheService.getMultiplierForRange("BMI", new BigDecimal("30.00"));
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.25"), result.get());
    }

    // --- Sabit Katsayı (getCoefficient) Testleri ---

    @Test
    @DisplayName("JOB_UNEMPLOYED katsayısı istendiğinde 1.10 dönmeli")
    void shouldReturnCorrectCoefficientForJobUnemployed() {
        Optional<BigDecimal> result = cacheService.getCoefficient("JOB_UNEMPLOYED");
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1.10"), result.get());
    }

    @Test
    @DisplayName("DISEASE_SEVERITY_COEFFICIENT istendiğinde 0.05 dönmeli")
    void shouldReturnCorrectCoefficientForDiseaseSeverity() {
        Optional<BigDecimal> result = cacheService.getCoefficient("DISEASE_SEVERITY_COEFFICIENT");
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("0.05"), result.get());
    }

    // --- Cache Dayanıklılık (Fault-Tolerance) Testi ---

    @Test
    @DisplayName("Servis yenileme anında hata alırsa eski önbelleği korumalı")
    void shouldRetainOldCacheWhenRefreshThrowsException() {
        // Önce cache dolu
        assertEquals(new BigDecimal("1.10"), cacheService.getCoefficient("JOB_UNEMPLOYED").orElseThrow());

        // Parameter-service çöktü simülasyonu
        when(parameterServiceClient.getAllPricingFactors()).thenThrow(new RuntimeException("Bağlantı koptu"));

        // Refresh tetiklendiğinde patlamamalı ve eski cache'i korumalı
        assertDoesNotThrow(() -> cacheService.refresh());
        assertEquals(new BigDecimal("1.10"), cacheService.getCoefficient("JOB_UNEMPLOYED").orElseThrow());
    }
}