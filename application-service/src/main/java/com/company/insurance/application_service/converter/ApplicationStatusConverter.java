package com.company.insurance.application_service.converter;

import com.company.insurance.application_service.enums.ApplicationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ApplicationStatusConverter implements AttributeConverter<ApplicationStatus, String> {
    @Override
    public String convertToDatabaseColumn(ApplicationStatus status){
        return status == null ? null : status.getCode();
    }

    @Override
    public ApplicationStatus convertToEntityAttribute(String code) {
        return code == null ? null : ApplicationStatus.fromCode(code);
    }
}
