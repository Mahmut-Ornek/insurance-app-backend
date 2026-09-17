package com.company.insurance.parameter_service.service;

import com.company.insurance.parameter_service.dto.ProvinceResponse;
import com.company.insurance.parameter_service.entity.Country;
import com.company.insurance.parameter_service.entity.Province;
import com.company.insurance.parameter_service.exception.CountryNotFoundException;
import com.company.insurance.parameter_service.exception.ProvinceNotFoundException;
import com.company.insurance.parameter_service.repository.CountryRepository;
import com.company.insurance.parameter_service.repository.ProvinceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final CountryRepository countryRepository;

    public ProvinceService(ProvinceRepository provinceRepository, CountryRepository countryRepository){
        this.provinceRepository = provinceRepository;
        this.countryRepository = countryRepository;
    }

    private ProvinceResponse toResponse(Province province, String countryName){
        return new ProvinceResponse(province.getProvinceId(), province.getName(),
                province.getCountryCode(), countryName, province.getCode());
    }

    public List<ProvinceResponse> getAll(){
        List<Province> provinces = provinceRepository.findAll();

        Set<String> countryCodes = provinces.stream()
                .map(Province::getCountryCode)
                .collect(Collectors.toSet());

        Map<String, String> countryNames = countryRepository.findAllById(countryCodes).stream()
                .collect(Collectors.toMap(Country::getCode, Country::getName));

        return provinces.stream()
                .map(province -> toResponse(province, countryNames.get(province.getCountryCode())))
                .toList();
    }

    public ProvinceResponse getById(Long id){
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new ProvinceNotFoundException(id));

        Country country = countryRepository.findById(province.getCountryCode())
                .orElseThrow(() -> new CountryNotFoundException(province.getCountryCode()));

        return toResponse(province, country.getName());
    }
}
