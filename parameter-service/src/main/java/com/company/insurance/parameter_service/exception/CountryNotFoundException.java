package com.company.insurance.parameter_service.exception;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String code) {
        super("Ülke bulunamadı: " + code);
    }
}
