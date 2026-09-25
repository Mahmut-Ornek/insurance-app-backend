package com.company.insurance.report_service.client;

import com.company.insurance.report_service.dto.CollectionPeriodSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class CollectionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CollectionServiceClient.class);
    private final RestClient collectionRestClient;

    public CollectionServiceClient(@Qualifier("collectionRestClient") RestClient collectionRestClient) {
        this.collectionRestClient = collectionRestClient;
    }

    public CollectionPeriodSummaryDto getSummary(LocalDateTime from, LocalDateTime to) {
        try {
            return collectionRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/collections/summary")
                            .queryParam("from", from.toString())
                            .queryParam("to", to.toString())
                            .build())
                    .retrieve()
                    .body(CollectionPeriodSummaryDto.class);
        } catch (Exception ex) {
            log.error("Collection-Service çağrısında hata oluştu: {}", ex.getMessage());
            return new CollectionPeriodSummaryDto(BigDecimal.ZERO, from, to);
        }
    }
}