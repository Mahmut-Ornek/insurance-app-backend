package com.company.insurance.product_service.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Ürün bulunamadı: " + id);
    }
}
