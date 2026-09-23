package com.company.insurance.collection_service.client;

import com.company.insurance.collection_service.dto.PolicyCreateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class PolicyServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PolicyServiceClient.class);
    private final RestClient restClient;

    public PolicyServiceClient(@Value("${services.policy-service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(8));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public void triggerPolicyCreation(Long applicationId, BigDecimal totalAmount, String currencyCode) {
        try {
            restClient.post()
                    .uri("/policies")
                    .body(new PolicyCreateRequestDto(applicationId, totalAmount, currencyCode))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Poliçe üretimi başarıyla tetiklendi. Application ID: {}", applicationId);
        } catch (Exception ex) {
            log.error("Poliçe oluşturma çağrısı başarısız oldu! Application ID: {}, Hata: {}",
                    applicationId, ex.getMessage());
        }
    }
}