package com.company.insurance.batch_service.client;

import com.company.insurance.batch_service.dto.EmailSendRequest;
import com.company.insurance.batch_service.dto.EmailSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Component
public class EmailServiceClient {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceClient.class);
    private final RestClient emailRestClient;
    private final ObjectMapper objectMapper;

    public EmailServiceClient(@Qualifier("emailRestClient") RestClient emailRestClient,
                              ObjectMapper objectMapper) {
        this.emailRestClient = emailRestClient;
        this.objectMapper = objectMapper;
    }

    public EmailSendResponse sendEmail(EmailSendRequest request) {
        try {
            // Yanıt tipine bakılmaksızın ham byte dizisi (byte[]) olarak çekiyoruz
            byte[] responseBytes = emailRestClient.post()
                    .uri("/emails/send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(byte[].class);

            if (responseBytes != null && responseBytes.length > 0) {
                return objectMapper.readValue(responseBytes, EmailSendResponse.class);
            }
        } catch (Exception ex) {
            log.error("Email servisi çağrısında hata oluştu: {}", ex.getMessage());
        }
        return null;
    }
}