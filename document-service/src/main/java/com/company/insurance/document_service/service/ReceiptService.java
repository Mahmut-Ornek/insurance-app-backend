package com.company.insurance.document_service.service;

import com.company.insurance.document_service.dto.ProcessedPaymentDto;
import com.company.insurance.document_service.dto.ReceiptCreateRequest;
import com.company.insurance.document_service.dto.ReceiptResponse;
import com.company.insurance.document_service.entity.Receipt;
import com.company.insurance.document_service.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Transactional(readOnly = true)
    public List<ProcessedPaymentDto> getProcessedPayments() {
        return receiptRepository.findAll().stream()
                .map(r -> new ProcessedPaymentDto(r.getCollectionPaymentId(), r.isEmailSent()))
                .toList();
    }

    public ReceiptResponse saveProcessedReceipt(ReceiptCreateRequest request) {
        Receipt receipt = receiptRepository.findByCollectionPaymentId(request.collectionPaymentId())
                .orElseGet(() -> {
                    Receipt r = new Receipt();
                    r.setCollectionPaymentId(request.collectionPaymentId());
                    r.setApplicationId(request.applicationId());
                    r.setCustomerEmail(request.customerEmail());
                    r.setAmount(request.amount());
                    r.setCurrencyCode(request.currencyCode());
                    r.setGeneratedAt(LocalDateTime.now());
                    r.setReceiptNumber("PENDING");
                    Receipt saved = receiptRepository.save(r);
                    saved.setReceiptNumber("MK-" + saved.getId());
                    return saved;
                });

        receipt.setEmailSent(request.emailSent());
        Receipt updated = receiptRepository.save(receipt);

        return toResponse(updated);
    }

    private ReceiptResponse toResponse(Receipt r) {
        return new ReceiptResponse(
                r.getId(),
                r.getCollectionPaymentId(),
                r.getReceiptNumber(),
                r.getApplicationId(),
                r.getCustomerEmail(),
                r.getAmount(),
                r.getCurrencyCode(),
                r.isEmailSent(),
                r.getGeneratedAt()
        );
    }
}