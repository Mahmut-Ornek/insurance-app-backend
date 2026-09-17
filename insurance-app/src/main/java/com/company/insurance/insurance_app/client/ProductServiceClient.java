package com.company.insurance.insurance_app.client;


import com.company.insurance.insurance_app.dto.BasePriceResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {
    private final RestClient restClient;

    public ProductServiceClient(@Qualifier("productServiceRestClient") RestClient restClient){this.restClient = restClient;}

    public BasePriceResponse getBasePrice(Long productId, Integer month, Integer year){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/baseprices/search")
                        .queryParam("productId", productId)
                        .queryParam("month", month)
                        .queryParam("year", year)
                        .build())
                .retrieve()
                .body(BasePriceResponse.class);
    }
}
