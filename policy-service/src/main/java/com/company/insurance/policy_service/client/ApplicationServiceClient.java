package com.company.insurance.policy_service.client;

import com.company.insurance.policy_service.dto.ApplicationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApplicationServiceClient {
    private final RestClient restClient;

    public ApplicationServiceClient(@Value("${services.application-service.url}") String baseUrl){
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public ApplicationDto getApplicationById(Long applicationId){
        return restClient.get().uri("/applications/{id}",applicationId).retrieve().body(ApplicationDto.class);
    }
}
