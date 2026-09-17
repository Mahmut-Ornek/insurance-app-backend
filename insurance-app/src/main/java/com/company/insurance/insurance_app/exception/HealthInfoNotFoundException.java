package com.company.insurance.insurance_app.exception;

public class HealthInfoNotFoundException extends RuntimeException {
    public HealthInfoNotFoundException(Long id) {
        super("Müşteriye ait sağlık bilgisi bulunamadı: " + id);
    }
}