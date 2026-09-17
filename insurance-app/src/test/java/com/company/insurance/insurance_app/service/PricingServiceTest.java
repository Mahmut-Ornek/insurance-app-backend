package com.company.insurance.insurance_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PricingServiceTest {
    private PricingService pricingService;
    private PricingFactorCacheService cacheServiceMock;

    @BeforeEach
    void setUp() {
        cacheServiceMock = Mockito.mock(PricingFactorCacheService.class);
        pricingService = new PricingService(null, null, cacheServiceMock, null, null, null, null, null);
    }

    @Test
    @DisplayName("Doğum tarihinden yaş doğru hesaplanıp AGE faktörüyle cache servisine iletilmeli")
    void shouldCalculateAgeCorrectlyAndCallCacheService() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        ArgumentCaptor<BigDecimal> ageCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        when(cacheServiceMock.getMultiplierForRange(eq("AGE"), ageCaptor.capture()))
                .thenReturn(Optional.of(new BigDecimal("1.2")));

        BigDecimal result = pricingService.calculateAgeMultiplier(birthDate);

        assertEquals(new BigDecimal("1.2"), result);
        assertEquals(new BigDecimal("30"), ageCaptor.getValue());
    }

    @Test
    @DisplayName("Doğum tarihi null verildiğinde cache servisine gitmeden varsayılan 1 dönmeli")
    void shouldReturnOneWithoutCallingCacheWhenBirthDateIsNull() {
        BigDecimal result = pricingService.calculateAgeMultiplier(null);

        assertEquals(BigDecimal.ONE, result);
        Mockito.verifyNoInteractions(cacheServiceMock);
    }

    @Test
    @DisplayName("Boy ve kilo verildiğinde BMI doğru hesaplanıp cache servisine iletilmeli")
    void shouldCalculateBmiCorrectlyAndCallCacheService() {
        // Boy: 200cm (2m), Kilo: 100kg -> BMI = 100 / (2 * 2) = 25.0
        BigDecimal heightCm = new BigDecimal("200");
        BigDecimal weightKg = new BigDecimal("100");
        ArgumentCaptor<BigDecimal> bmiCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        when(cacheServiceMock.getMultiplierForRange(eq("BMI"), bmiCaptor.capture()))
                .thenReturn(Optional.of(new BigDecimal("1.1")));

        BigDecimal result = pricingService.calculateBmiMultiplier(heightCm, weightKg);

        assertEquals(new BigDecimal("1.1"), result);
        assertEquals(25.0, bmiCaptor.getValue().doubleValue(), 0.01);
    }

    @Test
    @DisplayName("Boy sıfır veya negatif verildiğinde hesaplama yapılmadan 1 dönmeli")
    void shouldReturnOneWhenHeightIsZeroOrNegative() {
        BigDecimal result = pricingService.calculateBmiMultiplier(BigDecimal.ZERO, new BigDecimal("70"));

        assertEquals(BigDecimal.ONE, result);
        Mockito.verifyNoInteractions(cacheServiceMock);
    }

    @Test
    @DisplayName("Boy veya kilo null verildiğinde hesaplama yapılmadan 1 dönmeli")
    void shouldReturnOneWhenHeightOrWeightIsNull() {
        assertEquals(BigDecimal.ONE, pricingService.calculateBmiMultiplier(null, new BigDecimal("70")));
        assertEquals(BigDecimal.ONE, pricingService.calculateBmiMultiplier(new BigDecimal("180"), null));
        Mockito.verifyNoInteractions(cacheServiceMock);
    }
}
