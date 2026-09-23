package com.company.insurance.batch_service.client;

import com.company.insurance.batch_service.dto.ProcessedPaymentDto;
import com.company.insurance.batch_service.dto.ReceiptCreateRequest;
import com.company.insurance.batch_service.dto.ReceiptResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DocumentServiceClient {

    private final RestClient documentRestClient;

    public DocumentServiceClient(@Qualifier("documentRestClient") RestClient documentRestClient) {
        this.documentRestClient = documentRestClient;
    }

    public List<ProcessedPaymentDto> getProcessedPayments() {
        return documentRestClient.get()
                .uri("/receipts/processed-summary")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProcessedPaymentDto>>() {});
    }

    public ReceiptResponse saveReceipt(ReceiptCreateRequest request) {
        return documentRestClient.post()
                .uri("/receipts")
                .body(request)
                .retrieve()
                .body(ReceiptResponse.class);
    }
}