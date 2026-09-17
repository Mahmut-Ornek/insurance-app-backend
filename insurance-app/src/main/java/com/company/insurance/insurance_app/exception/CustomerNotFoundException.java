package com.company.insurance.insurance_app.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(Long id){
        super("Müşteri bulunamadı: " + id);
    }
}
