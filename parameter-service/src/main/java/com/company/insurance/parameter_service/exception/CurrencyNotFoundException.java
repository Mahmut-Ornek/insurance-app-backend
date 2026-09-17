package com.company.insurance.parameter_service.exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String code) {
        super("Para birimi bulunamadı: " + code);
    }
}
