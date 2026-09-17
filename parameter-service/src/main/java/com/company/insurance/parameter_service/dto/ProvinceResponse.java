package com.company.insurance.parameter_service.dto;

public record ProvinceResponse(Long provinceId,
                               String name,
                               String countryCode,
                               String countryName,
                               String provinceCode) {
}
