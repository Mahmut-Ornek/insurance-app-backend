package com.company.insurance.document_service.controller;

import com.company.insurance.document_service.dto.BatchReceiptResponse;
import com.company.insurance.document_service.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/generate-batch")
    public ResponseEntity<BatchReceiptResponse> generateBatchReceipts() {
        BatchReceiptResponse response = receiptService.generateBatchReceipts();
        return ResponseEntity.ok(response);
    }
}