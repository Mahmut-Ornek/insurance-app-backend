package com.company.insurance.parameter_service.exception;

public class ApplicationStatusNotFoundException extends RuntimeException {
    public ApplicationStatusNotFoundException(String code) {

        super("Aplikasyon statüsü bulunamadı: " + code);
    }
}
