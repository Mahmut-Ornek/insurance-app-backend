package com.company.insurance.insurance_app.controller;


import com.company.insurance.insurance_app.dto.PriceCalculationRequest;
import com.company.insurance.insurance_app.dto.PriceCalculationResponse;
import com.company.insurance.insurance_app.service.PricingFactorCacheService;
import com.company.insurance.insurance_app.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pricing")
public class PricingController {
    private final PricingService pricingService;
    private final PricingFactorCacheService cacheService;

    public PricingController(PricingService pricingService, PricingFactorCacheService cacheService){
        this.pricingService = pricingService;
        this.cacheService = cacheService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@Valid @RequestBody PriceCalculationRequest request){
        PriceCalculationResponse response = pricingService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-cache")
    public ResponseEntity<String> refreshCache() {
        cacheService.refresh();
        return ResponseEntity.ok("Önbellek başarıyla yenilendi.");
    }
}
