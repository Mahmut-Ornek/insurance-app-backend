package com.company.insurance.document_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient collectionRestClient(@Value("${services.collection-service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient applicationRestClient(@Value("${services.application-service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient insuranceAppRestClient(@Value("${services.insurance-app.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient emailRestClient(@Value("${services.email-service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}