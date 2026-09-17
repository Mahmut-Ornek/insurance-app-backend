package com.company.insurance.parameter_service.controller;


import com.company.insurance.parameter_service.dto.ExchangeRateResponse;
import com.company.insurance.parameter_service.service.ExchangeRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exchange-rates")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService){
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<ExchangeRateResponse> getRate(@PathVariable String currencyCode){
        return ResponseEntity.ok(exchangeRateService.getRate(currencyCode));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncRates(){
        exchangeRateService.syncRatesFromTcmb();
        return ResponseEntity.ok("TCMB kurları başarıyla güncellendi.");
    }
}
