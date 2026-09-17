package com.company.insurance.parameter_service.service;

import com.company.insurance.parameter_service.dto.CurrencyResponse;
import com.company.insurance.parameter_service.entity.Currency;
import com.company.insurance.parameter_service.exception.CurrencyNotFoundException;
import com.company.insurance.parameter_service.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository){this.currencyRepository = currencyRepository;}

    private CurrencyResponse toResponse(Currency currency){
        return new CurrencyResponse(currency.getCode(),
                currency.getName(), currency.getDefinition());
    }

    public List<CurrencyResponse> getAll(){
        List<Currency> currencies = currencyRepository.findAll();
        List<CurrencyResponse> responses = new ArrayList<>();

        for(Currency currency : currencies){
            responses.add(toResponse(currency));
        }

        return responses;
    }

    public CurrencyResponse getByCode(String code){
        Currency currency = currencyRepository.findById(code)
                .orElseThrow(() -> new CurrencyNotFoundException(code));

        return toResponse(currency);
    }


}
