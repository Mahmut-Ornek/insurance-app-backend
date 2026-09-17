package com.company.insurance.product_service.exception;

public class BasePriceNotFoundException extends RuntimeException {
    public BasePriceNotFoundException(Long id) {
        super("Baz ücret bulunamadı: " + id);
    }
}
