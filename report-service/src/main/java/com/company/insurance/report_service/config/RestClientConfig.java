package com.company.insurance.report_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    private SimpleClientHttpRequestFactory createFactory(int timeoutSeconds) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(timeoutSeconds));
        factory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));
        return factory;
    }

    @Bean
    public RestClient collectionRestClient(
            @Value("${services.collection.url:http://localhost:8084}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(createFactory(10))
                .build();
    }

    @Bean
    public RestClient policyRestClient(
            @Value("${services.policy.url:http://localhost:8088}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(createFactory(10))
                .build();
    }

    @Bean
    public RestClient productRestClient(
            @Value("${services.product.url:http://localhost:8081}") String url) {
        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(createFactory(11))
                .build();
    }
}