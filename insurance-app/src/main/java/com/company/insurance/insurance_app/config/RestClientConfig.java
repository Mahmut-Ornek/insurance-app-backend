package com.company.insurance.insurance_app.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${parameter.service.url}")
    private String parameterServiceUrl;

    @Bean
    public RestClient productServiceRestClient(){
        return RestClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    @Bean
    public RestClient parameterServiceRestClient(){
        return RestClient.builder()
                .baseUrl(parameterServiceUrl)
                .build();
    }
}
