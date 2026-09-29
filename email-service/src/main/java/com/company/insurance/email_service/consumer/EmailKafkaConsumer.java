package com.company.insurance.email_service.consumer;

import com.company.insurance.email_service.dto.EmailSendRequest;
import com.company.insurance.email_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailKafkaConsumer.class);

    private final EmailService emailService;

    public EmailKafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-requests", groupId = "email-service-group-v2")
    public void consumeEmailRequest(EmailSendRequest request) {
        log.info("Kafka'dan e-posta talebi alindi: Alici={}, Konu={}", request.to(), request.subject());
        try {
            emailService.sendEmail(request);
            log.info("E-posta arka planda basariyla iletildi: Alici={}", request.to());
        } catch (Exception ex) {
            log.error("Kafka e-posta gonderiminde hata olustu: {}", ex.getMessage(), ex);
        }
    }
}