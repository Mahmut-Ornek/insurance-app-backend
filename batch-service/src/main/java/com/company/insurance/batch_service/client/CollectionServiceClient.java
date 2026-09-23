package com.company.insurance.batch_service.client;

import com.company.insurance.batch_service.dto.PaymentSummaryDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;

@Component
public class CollectionServiceClient {

    private final RestClient collectionRestClient;

    public CollectionServiceClient(@Qualifier("collectionRestClient") RestClient collectionRestClient) {
        this.collectionRestClient = collectionRestClient;
    }

    public List<PaymentSummaryDto> getAllPayments() {
        return collectionRestClient.get()
                .uri("/payments")
                .retrieve()
                .body(new ParameterizedTypeReference<List<PaymentSummaryDto>>() {});
    }
}