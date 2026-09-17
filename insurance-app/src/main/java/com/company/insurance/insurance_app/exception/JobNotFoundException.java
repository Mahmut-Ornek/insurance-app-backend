package com.company.insurance.insurance_app.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {
        super("İş bulunamadı: " + id);
    }
}
