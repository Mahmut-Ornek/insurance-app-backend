package com.company.insurance.batch_service.dto;

import java.time.LocalDateTime;

public record EmailSendResponse(
        Long logId,
        String to,
        String subject,
        boolean success,
        String message,
        LocalDateTime sentAt
) {}