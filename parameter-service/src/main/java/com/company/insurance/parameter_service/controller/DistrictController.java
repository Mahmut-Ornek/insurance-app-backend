package com.company.insurance.parameter_service.controller;


import com.company.insurance.parameter_service.dto.DistrictResponse;
import com.company.insurance.parameter_service.service.DistrictService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/districts")
public class DistrictController {
    private final DistrictService districtService;

    public DistrictController(DistrictService districtService){this.districtService = districtService;}

    @GetMapping
    public List<DistrictResponse> getAll(){return districtService.getAll();}

    @GetMapping("/{id}")
    public DistrictResponse getById(@PathVariable Long id){return districtService.getById(id);}
}
