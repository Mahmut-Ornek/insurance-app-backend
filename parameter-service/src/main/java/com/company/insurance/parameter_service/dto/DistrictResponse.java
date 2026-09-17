package com.company.insurance.parameter_service.dto;

public record DistrictResponse(Long districtId,
                               String name,
                               Long provinceId,
                               String provinceName,
                               String districtCode) {
}
