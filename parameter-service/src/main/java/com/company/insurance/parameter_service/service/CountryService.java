package com.company.insurance.parameter_service.service;


import com.company.insurance.parameter_service.dto.CountryResponse;
import com.company.insurance.parameter_service.entity.Country;
import com.company.insurance.parameter_service.exception.CountryNotFoundException;
import com.company.insurance.parameter_service.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository){this.countryRepository = countryRepository;}

    private CountryResponse toResponse(Country country){
        return new CountryResponse(country.getCode(), country.getName(), country.getDefinition());
    }

    public List<CountryResponse> getAll(){
        List<CountryResponse> responses = countryRepository.findAll().stream().map(this::toResponse).toList();
        return responses;
    }

    public CountryResponse getByCode(String code){
        Country country = countryRepository.findById(code)
                .orElseThrow(() -> new CountryNotFoundException(code));

        return toResponse(country);
    }
}
