package com.company.insurance.document_service.dto;

public record CustomerDto(
        Long customerId,
        String name,
        String surname,
        String email
) {}