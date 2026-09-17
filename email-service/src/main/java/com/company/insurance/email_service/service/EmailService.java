package com.company.insurance.email_service.service;

import com.company.insurance.email_service.dto.EmailSendRequest;
import com.company.insurance.email_service.dto.EmailSendResponse;
import com.company.insurance.email_service.entity.Email;
import com.company.insurance.email_service.repository.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    public EmailService(JavaMailSender mailSender, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }

    public EmailSendResponse sendEmail(EmailSendRequest request) {
        LocalDateTime now = LocalDateTime.now();
        boolean success = false;
        String failureReason = null;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.to());
            helper.setSubject(request.subject());
            helper.setText(request.body(), true);

            mailSender.send(message);
            success = true;
            log.info("Email basariyla gonderildi. Alici: {}, Konu: {}", request.to(), request.subject());

        } catch (Exception ex) {
            log.error("Email gonderimi basarisiz! Alici: {}, Hata: {}", request.to(), ex.getMessage());
            failureReason = ex.getMessage() != null && ex.getMessage().length() > 500
                    ? ex.getMessage().substring(0, 500)
                    : ex.getMessage();
        }

        Email email = new Email();
        email.setRecipient(request.to());
        email.setSubject(request.subject());
        email.setBody(request.body());
        email.setSent(success);
        email.setFailureReason(failureReason);
        email.setAttemptedAt(now);

        Email savedLog = emailRepository.save(email);

        String statusMessage = success
                ? "Email başarıyla iletildi."
                : "Email gönderimi başarısız oldu: " + failureReason;

        return new EmailSendResponse(
                savedLog.getId(),
                request.to(),
                request.subject(),
                success,
                statusMessage,
                now
        );
    }
}