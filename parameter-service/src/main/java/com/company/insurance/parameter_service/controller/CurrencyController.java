package com.company.insurance.parameter_service.controller;


import com.company.insurance.parameter_service.dto.CurrencyResponse;
import com.company.insurance.parameter_service.service.CurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService){this.currencyService = currencyService;}

    @GetMapping
    public List<CurrencyResponse> getAll(){return currencyService.getAll();}

    @GetMapping("/{code}")
    public CurrencyResponse getByCode(@PathVariable String code){return currencyService.getByCode(code);}
}
