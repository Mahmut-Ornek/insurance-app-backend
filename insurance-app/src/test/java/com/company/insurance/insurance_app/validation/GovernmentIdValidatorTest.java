package com.company.insurance.insurance_app.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class GovernmentIdValidatorTest {

    @Test
    @DisplayName("Bütün koşulları sağlıyorsa true dönmeli")
    void shouldReturnTrueWhenMeetsAllTheConditions(){
        assertTrue(GovernmentIdValidator.isValid("98556894532"));
    }

    @Test
    @DisplayName("Null verildiğinde false dönmeli")
    void shouldReturnFalseWhenInputIsNull(){
        assertFalse(GovernmentIdValidator.isValid(null));
    }

    @Test
    @DisplayName("Boş metin olursa false dönmeli")
    void shouldReturnFalseWhenInputIsEmptyString() {assertFalse(GovernmentIdValidator.isValid(""));}

    @Test
    @DisplayName("TCKN olması gerekenden kısa olduğunda false dönmeli")
    void shouldReturnFalseWhenInputIsTooShort() {assertFalse(GovernmentIdValidator.isValid("1234567890"));}

    @Test
    @DisplayName("TCKN olması gerekenden uzun olduğunda false dönmeli")
    void shouldReturnFalseWhenInputIsTooLong() {assertFalse(GovernmentIdValidator.isValid("123456789012"));}

    @Test
    @DisplayName("Harf içerirse false dönmeli")
    void shouldReturnFalseWhenIncludesLetter() {assertFalse(GovernmentIdValidator.isValid("12345a78901"));}

    @Test
    @DisplayName("Boşluk içerirse false dönmeli")
    void shouldReturnFalseWhenIncludesSpace() {assertFalse(GovernmentIdValidator.isValid("12345 78901"));}

    @Test
    @DisplayName("Özel karakter varsa false dönmeli")
    void shouldReturnFalseWhenIncludesSpecialCharacter() {assertFalse(GovernmentIdValidator.isValid("12345-78901"));}

    @Test
    @DisplayName("Son hane yanlış")
    void shouldReturnFalseWhenLastNumberIsIncorrect() {assertFalse(GovernmentIdValidator.isValid("12345678951"));}

    @Test
    @DisplayName("İlk rakam 0 ise false dönmeli")
    void shouldReturnFalseWhenStartsWithZero() {assertFalse(GovernmentIdValidator.isValid("01285222554"));}

}