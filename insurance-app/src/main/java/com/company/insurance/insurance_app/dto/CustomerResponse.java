package com.company.insurance.insurance_app.dto;

import java.time.LocalDate;

public record CustomerResponse(Long customerId,
                               String name,
                               String surname,
                               String governmentId,
                               String email,
                               Long jobId,
                               String jobName,
                               String motherName,
                               String fatherName,
                               LocalDate birthDate,
                               String address,
                               String username,
                               boolean isDeleted,
                               String createdBy) {
}