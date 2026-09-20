package com.company.insurance.document_service.client;

import com.company.insurance.document_service.dto.EmailSendRequest;
import com.company.insurance.document_service.dto.EmailSendResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EmailServiceClient {

    private final RestClient emailRestClient;

    public EmailServiceClient(RestClient emailRestClient) {
        this.emailRestClient = emailRestClient;
    }

    public EmailSendResponse sendEmail(EmailSendRequest request) {
        return emailRestClient.post()
                .uri("/emails/send")
                .body(request)
                .retrieve()
                .body(EmailSendResponse.class);
    }
}