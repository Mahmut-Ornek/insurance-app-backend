package com.company.insurance.parameter_service.controller;


import com.company.insurance.parameter_service.dto.PricingFactorResponse;
import com.company.insurance.parameter_service.dto.UpdatePricingFactorRequest;
import com.company.insurance.parameter_service.service.PricingFactorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricingfactors")
public class PricingFactorController {
    private final PricingFactorService pricingFactorService;

    public PricingFactorController(PricingFactorService pricingFactorService){
        this.pricingFactorService = pricingFactorService;
    }

    @GetMapping
    public ResponseEntity<List<PricingFactorResponse>> getAll(){
        return ResponseEntity.ok(pricingFactorService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PricingFactorResponse> updateMultiplier(@PathVariable Long id,
            @Valid @RequestBody UpdatePricingFactorRequest request) {
        return ResponseEntity.ok(pricingFactorService.updateMultiplier(id, request));
    }
}
