package com.company.insurance.payment_service.controller;

import com.company.insurance.payment_service.dto.PaymentInitRequest;
import com.company.insurance.payment_service.dto.PaymentInitResponse;
import com.company.insurance.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){this.paymentService = paymentService;}

    @PostMapping("/initialize")
    public ResponseEntity<PaymentInitResponse> initialize(@Valid @RequestBody PaymentInitRequest request) {
        return ResponseEntity.ok(paymentService.initializePayment(request));
    }

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("token") String token) {
        return ResponseEntity.ok(paymentService.handleCallback(token));
    }
}
