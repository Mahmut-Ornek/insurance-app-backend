package com.company.insurance.insurance_app.exception;

public class DiseaseNotFoundException extends RuntimeException {
    public DiseaseNotFoundException(Long id) {
        super("Hastalık bulunamadı: " + id);
    }
}
