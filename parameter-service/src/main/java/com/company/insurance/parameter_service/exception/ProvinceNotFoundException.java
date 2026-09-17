package com.company.insurance.parameter_service.exception;

public class ProvinceNotFoundException extends RuntimeException {
    public ProvinceNotFoundException(Long provinceId) {
        super("İl bulunamadı: " + provinceId);
    }
}
