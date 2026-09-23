package com.company.insurance.email_service.controller;

import com.company.insurance.email_service.dto.EmailSendRequest;
import com.company.insurance.email_service.dto.EmailSendResponse;
import com.company.insurance.email_service.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class EmailController {
    private final EmailService emailService;
    public EmailController(EmailService emailService){this.emailService =emailService;}

    @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailSendResponse> sendEmail(@Valid @RequestBody EmailSendRequest request){
        EmailSendResponse response = emailService.sendEmail(request);
        return ResponseEntity.ok(response);
    }
}
