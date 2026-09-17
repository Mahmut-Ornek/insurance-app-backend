package com.company.insurance.insurance_app.service;

import com.company.insurance.insurance_app.dto.HealthDiseaseItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HealthServiceTest {
    private HealthService healthService;

    @BeforeEach
    void setUp(){
        healthService = new HealthService(null, null);
    }

    @Test
    @DisplayName("Kronik hastalık var ve hastalık listesi dolu ise hata fırlatmamalı")
    void shouldPassWhenChronicDiseaseIsTrueAndListIsValid() {
        List<HealthDiseaseItemDto> diseases = List.of(
                new HealthDiseaseItemDto(1L, LocalDate.now(), "Açıklama 1"),
                new HealthDiseaseItemDto(2L, LocalDate.now(), "Açıklama 2")
        );

        assertDoesNotThrow(() -> healthService.validateChronicDiseaseConsistency(true, diseases));
    }

    @Test
    @DisplayName("Kronik hastalık var denilip hastalık listesi null verilirse hata fırlatmalı")
    void shouldThrowExceptionWhenChronicDiseaseIsTrueAndListIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> healthService.validateChronicDiseaseConsistency(true, null)
        );

        assertEquals("Kronik hastalık belirtildiğinde en az bir hastalık detayı girilmelidir.", exception.getMessage());
    }

    @Test
    @DisplayName("Kronik hastalık var denilip hastalık listesi boş verilirse hata fırlatmalı")
    void shouldThrowExceptionWhenChronicDiseaseIsTrueAndListIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> healthService.validateChronicDiseaseConsistency(true, Collections.emptyList())
        );

        assertEquals("Kronik hastalık belirtildiğinde en az bir hastalık detayı girilmelidir.", exception.getMessage());
    }

    @Test
    @DisplayName("Kronik hastalık yok denilip hastalık listesi dolu verilirse hata fırlatmalı")
    void shouldThrowExceptionWhenChronicDiseaseIsFalseAndListIsNotEmpty() {
        List<HealthDiseaseItemDto> diseases = List.of(
                new HealthDiseaseItemDto(1L, LocalDate.now(), "Açıklama")
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> healthService.validateChronicDiseaseConsistency(false, diseases)
        );

        assertEquals("Kronik hastalık bulunmadığı belirtilmişken hastalık listesi gönderilemez.", exception.getMessage());
    }

    @Test
    @DisplayName("Kronik hastalık yok ve liste boş ise geçerli kabul edilmeli")
    void shouldPassWhenChronicDiseaseIsFalseAndListIsEmpty() {
        assertDoesNotThrow(() -> healthService.validateChronicDiseaseConsistency(false, Collections.emptyList()));
    }

    @Test
    @DisplayName("Kronik hastalık yok ve liste null ise geçerli kabul edilmeli")
    void shouldPassWhenChronicDiseaseIsFalseAndListIsNull() {
        assertDoesNotThrow(() -> healthService.validateChronicDiseaseConsistency(false, null));
    }

    @Test
    @DisplayName("Aynı hastalık ID'si listede birden fazla kez yer alıyorsa hata fırlatmalı")
    void shouldThrowExceptionWhenDuplicateDiseaseIdProvided() {
        List<HealthDiseaseItemDto> duplicateDiseases = List.of(
                new HealthDiseaseItemDto(1L, LocalDate.now(), "İlk kayıt"),
                new HealthDiseaseItemDto(1L, LocalDate.now(), "Tekrar eden kayıt")
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> healthService.validateChronicDiseaseConsistency(true, duplicateDiseases)
        );

        assertEquals("Aynı hastalık listede birden fazla kez yer alamaz.", exception.getMessage());
    }
}
