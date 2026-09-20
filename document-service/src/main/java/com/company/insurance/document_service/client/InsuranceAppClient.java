package com.company.insurance.document_service.client;

import com.company.insurance.document_service.dto.CustomerDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InsuranceAppClient {

    private final RestClient insuranceAppRestClient;

    public InsuranceAppClient(RestClient insuranceAppRestClient) {
        this.insuranceAppRestClient = insuranceAppRestClient;
    }

    public CustomerDto getCustomerById(Long id) {
        return insuranceAppRestClient.get()
                .uri("/customers/{id}", id)
                .retrieve()
                .body(CustomerDto.class);
    }
}