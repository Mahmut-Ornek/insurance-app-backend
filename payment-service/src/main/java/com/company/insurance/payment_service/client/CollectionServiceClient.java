package com.company.insurance.payment_service.client;

import com.company.insurance.payment_service.dto.CollectionPaymentRequest;
import com.company.insurance.payment_service.dto.CollectionSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class CollectionServiceClient {
    private static final Logger log = LoggerFactory.getLogger(CollectionServiceClient.class);
    private final RestClient restClient;

    public CollectionServiceClient(@Value("${services.collection-service.url}") String collectionServiceUrl){
        this.restClient = RestClient.builder()
                .baseUrl(collectionServiceUrl)
                .build();
    }

    public void notifyPaymentSuccess(Long collectionId, BigDecimal amount, String paymentReference) {
        CollectionPaymentRequest request = new CollectionPaymentRequest(amount, LocalDateTime.now(), paymentReference);


        log.info("Collection-Service'e odeme bildiriliyor. CollectionId: {}, Tutar: {}", collectionId, amount);
        restClient.post()
                .uri("/collections/{id}/payments", collectionId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public CollectionSummaryDto getCollection(Long collectionId) {
        return restClient.get()
                .uri("/collections/{id}", collectionId)
                .retrieve()
                .body(CollectionSummaryDto.class);
    }
}
