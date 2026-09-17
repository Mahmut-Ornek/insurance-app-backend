package com.company.insurance.parameter_service.controller;


import com.company.insurance.parameter_service.dto.CountryResponse;
import com.company.insurance.parameter_service.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService){this.countryService = countryService;}

    @GetMapping
    public List<CountryResponse> getAll() {return countryService.getAll();}

    @GetMapping("/{code}")
    public CountryResponse getByCode(@PathVariable String code){return countryService.getByCode(code);}
}
