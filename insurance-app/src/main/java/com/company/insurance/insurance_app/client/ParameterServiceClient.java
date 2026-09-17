package com.company.insurance.insurance_app.client;


import com.company.insurance.insurance_app.dto.ExchangeRateResponse;
import com.company.insurance.insurance_app.dto.PricingFactorResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ParameterServiceClient {
    private final RestClient restClient;

    public ParameterServiceClient(@Qualifier("parameterServiceRestClient") RestClient restClient){
        this.restClient = restClient;
    }

    public List<PricingFactorResponse> getAllPricingFactors(){
        return restClient.get()
                .uri("/pricingfactors")
                .retrieve()
                .body(new ParameterizedTypeReference<List<PricingFactorResponse>>() {});
    }

    public ExchangeRateResponse getExchangeRate(String currencyCode) {
        return restClient.get()
                .uri("/exchange-rates/{currencyCode}", currencyCode)
                .retrieve()
                .body(ExchangeRateResponse.class);
    }
}
