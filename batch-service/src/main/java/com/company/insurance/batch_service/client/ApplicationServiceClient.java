package com.company.insurance.batch_service.client;

import com.company.insurance.batch_service.dto.ApplicationDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApplicationServiceClient {

    private final RestClient applicationRestClient;

    public ApplicationServiceClient(@Qualifier("applicationRestClient") RestClient applicationRestClient) {
        this.applicationRestClient = applicationRestClient;
    }

    public ApplicationDto getApplicationById(Long id) {
        return applicationRestClient.get()
                .uri("/applications/{id}", id)
                .retrieve()
                .body(ApplicationDto.class);
    }
}