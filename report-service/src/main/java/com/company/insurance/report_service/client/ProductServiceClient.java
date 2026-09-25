package com.company.insurance.report_service.client;

import com.company.insurance.report_service.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class ProductServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);
    private final RestClient productRestClient;

    public ProductServiceClient(@Qualifier("productRestClient") RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    public List<ProductDto> getAllProducts() {
        try {
            return productRestClient.get()
                    .uri("/products")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception ex) {
            log.error("Product-Service çağrısında hata oluştu: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}