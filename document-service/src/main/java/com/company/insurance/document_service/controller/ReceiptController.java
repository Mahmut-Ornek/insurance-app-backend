package com.company.insurance.document_service.controller;

import com.company.insurance.document_service.dto.ProcessedPaymentDto;
import com.company.insurance.document_service.dto.ReceiptCreateRequest;
import com.company.insurance.document_service.dto.ReceiptResponse;
import com.company.insurance.document_service.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/processed-summary")
    public ResponseEntity<List<ProcessedPaymentDto>> getProcessedPayments() {
        return ResponseEntity.ok(receiptService.getProcessedPayments());
    }

    @PostMapping
    public ResponseEntity<ReceiptResponse> createReceipt(@Valid @RequestBody ReceiptCreateRequest request) {
        return ResponseEntity.ok(receiptService.saveProcessedReceipt(request));
    }
}