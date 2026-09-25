package com.company.insurance.report_service.client;

import com.company.insurance.report_service.dto.ProductSalesCountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class PolicyServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PolicyServiceClient.class);
    private final RestClient policyRestClient;

    public PolicyServiceClient(@Qualifier("policyRestClient") RestClient policyRestClient) {
        this.policyRestClient = policyRestClient;
    }

    public List<ProductSalesCountDto> getSummary(LocalDate from, LocalDate to) {
        try {
            return policyRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/policies/summary")
                            .queryParam("from", from.toString())
                            .queryParam("to", to.toString())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception ex) {
            log.error("Policy-Service çağrısında hata oluştu: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}