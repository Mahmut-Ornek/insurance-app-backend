package com.company.insurance.batch_service.dto;

public record EmailSendRequest(
        String to,
        String subject,
        String body
) {}