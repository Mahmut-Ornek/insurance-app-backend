package com.company.insurance.parameter_service.service;


import com.company.insurance.parameter_service.dto.DistrictResponse;
import com.company.insurance.parameter_service.entity.District;
import com.company.insurance.parameter_service.entity.Province;
import com.company.insurance.parameter_service.exception.DistrictNotFoundException;
import com.company.insurance.parameter_service.exception.ProvinceNotFoundException;
import com.company.insurance.parameter_service.repository.DistrictRepository;
import com.company.insurance.parameter_service.repository.ProvinceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DistrictService {
    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;

    public DistrictService(DistrictRepository districtRepository, ProvinceRepository provinceRepository){
        this.districtRepository = districtRepository;
        this.provinceRepository = provinceRepository;
    }

    private DistrictResponse toResponse(District district, String provinceName){
        return new DistrictResponse(district.getDistrictId(), district.getName(), district.getProvinceId(), provinceName,
                district.getCode());
    }

    public List<DistrictResponse> getAll(){
        List<District> districts = districtRepository.findAll();

        Set<Long> provinceIds = districts.stream()
                .map(District::getProvinceId)
                .collect(Collectors.toSet());

        Map<Long, String> provinceNames = provinceRepository.findAllById(provinceIds).stream()
                .collect(Collectors.toMap(Province::getProvinceId, Province::getName));

        return districts.stream()
                .map(district -> toResponse(district, provinceNames.get(district.getProvinceId())))
                .toList();
    }

    public DistrictResponse getById(Long id){
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new DistrictNotFoundException(id));

        Province province = provinceRepository.findById(district.getProvinceId())
                .orElseThrow(() -> new ProvinceNotFoundException(district.getProvinceId()));

        return toResponse(district, province.getName());
    }
}
