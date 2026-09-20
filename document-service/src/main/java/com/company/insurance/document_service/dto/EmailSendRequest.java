package com.company.insurance.document_service.dto;

public record EmailSendRequest(
        String to,
        String subject,
        String body
) {}