package com.company.insurance.document_service.client;

import com.company.insurance.document_service.dto.PaymentSummaryDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;

@Component
public class CollectionServiceClient {

    private final RestClient collectionRestClient;

    public CollectionServiceClient(RestClient collectionRestClient) {
        this.collectionRestClient = collectionRestClient;
    }

    public List<PaymentSummaryDto> getAllPayments() {
        return collectionRestClient.get()
                .uri("/payments")
                .retrieve()
                .body(new ParameterizedTypeReference<List<PaymentSummaryDto>>() {});
    }
}