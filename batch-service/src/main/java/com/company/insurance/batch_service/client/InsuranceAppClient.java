package com.company.insurance.batch_service.client;

import com.company.insurance.batch_service.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InsuranceAppClient {

    private final RestClient insuranceAppRestClient;

    public InsuranceAppClient(@Qualifier("insuranceAppRestClient") RestClient insuranceAppRestClient) {
        this.insuranceAppRestClient = insuranceAppRestClient;
    }

    public CustomerDto getCustomerById(Long id) {
        return insuranceAppRestClient.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .body(CustomerDto.class);
    }
}