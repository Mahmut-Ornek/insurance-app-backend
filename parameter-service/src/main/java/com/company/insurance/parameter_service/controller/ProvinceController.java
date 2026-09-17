package com.company.insurance.parameter_service.controller;

import com.company.insurance.parameter_service.dto.ProvinceResponse;
import com.company.insurance.parameter_service.service.ProvinceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/provinces")
public class ProvinceController {
    private final ProvinceService provinceService;

    public ProvinceController(ProvinceService provinceService){this.provinceService = provinceService;}

    @GetMapping
    public List<ProvinceResponse> getAll(){return provinceService.getAll();}

    @GetMapping("/{id}")
    public ProvinceResponse getById(@PathVariable Long id){return provinceService.getById(id);}
}
