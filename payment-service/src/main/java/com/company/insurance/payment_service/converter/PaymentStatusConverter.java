package com.company.insurance.payment_service.converter;

import com.company.insurance.payment_service.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String code) {
        return code == null ? null : PaymentStatus.fromCode(code);
    }
}