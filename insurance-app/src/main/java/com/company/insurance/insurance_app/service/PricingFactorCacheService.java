package com.company.insurance.insurance_app.service;


import com.company.insurance.insurance_app.client.ParameterServiceClient;
import com.company.insurance.insurance_app.dto.PricingFactorResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PricingFactorCacheService {
    private static final Logger log = LoggerFactory.getLogger(PricingFactorCacheService.class);

    private final ParameterServiceClient parameterServiceClient;

    private volatile List<PricingFactorResponse> cachedFactors = Collections.emptyList();

    public PricingFactorCacheService(ParameterServiceClient parameterServiceClient){
        this.parameterServiceClient = parameterServiceClient;
    }

    @PostConstruct
    public void init(){
        refresh();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) //30 dakika
    public void refresh(){
        try {
            List<PricingFactorResponse> fresh = parameterServiceClient.getAllPricingFactors();
            if (fresh != null && !fresh.isEmpty()){
                this.cachedFactors = fresh;
                log.info("Pricing factors cache başarıyla güncellendi. Eleman sayısı: {}", fresh.size());
            }
        } catch (Exception e){
            log.warn("Parameter-Service'e erişilemedi, mevcut önbellek korunuyor. Hata: {}", e.getMessage());
        }
    }

    public Optional<BigDecimal> getMultiplierForRange(String factorType, BigDecimal value){
        if (value == null) return Optional.empty();

        return cachedFactors.stream().filter(f -> factorType.equals(f.factorType()))
                .filter(f -> (f.minValue() == null || value.compareTo(f.minValue()) >= 0)
                        && (f.maxValue() == null || value.compareTo(f.maxValue()) <= 0)).map(PricingFactorResponse::multiplier)
                .findFirst();
    }

    public Optional<BigDecimal> getCoefficient(String factorType){
        return cachedFactors.stream()
                .filter(f -> factorType.equals(f.factorType()))
                .map(PricingFactorResponse::multiplier).findFirst();
    }
}
