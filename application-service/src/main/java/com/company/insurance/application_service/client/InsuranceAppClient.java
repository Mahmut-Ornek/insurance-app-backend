package com.company.insurance.application_service.client;


import com.company.insurance.application_service.dto.PriceCalculationRequest;
import com.company.insurance.application_service.dto.PriceCalculationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InsuranceAppClient {
    private final RestClient restClient;

    public InsuranceAppClient(@Value("${services.insurance-app.url}") String insuranceAppUrl){
        this.restClient = RestClient.builder()
                .baseUrl(insuranceAppUrl)
                .build();
    }

    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request){
        return restClient.post()
                .uri("/pricing/calculate")
                .body(request)
                .retrieve()
                .body(PriceCalculationResponse.class);
    }
}
