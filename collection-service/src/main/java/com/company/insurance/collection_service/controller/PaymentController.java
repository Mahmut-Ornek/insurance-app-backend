package com.company.insurance.collection_service.controller;

import com.company.insurance.collection_service.dto.PaymentSummaryResponse;
import com.company.insurance.collection_service.service.CollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final CollectionService collectionService;

    public PaymentController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentSummaryResponse>> getAllPayments() {
        return ResponseEntity.ok(collectionService.getAllPayments());
    }
}