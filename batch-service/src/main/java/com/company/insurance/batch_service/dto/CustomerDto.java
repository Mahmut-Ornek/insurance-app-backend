package com.company.insurance.batch_service.dto;

public record CustomerDto(
        Long customerId,
        String name,
        String surname,
        String email
) {}