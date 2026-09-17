package com.company.insurance.parameter_service.controller;


import com.company.insurance.parameter_service.dto.ApplicationStatusResponse;
import com.company.insurance.parameter_service.service.ApplicationStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("application-status")
public class ApplicationStatusController {
    private final ApplicationStatusService applicationStatusService;

    public ApplicationStatusController(ApplicationStatusService applicationStatusService){
        this.applicationStatusService = applicationStatusService;
    }

    @GetMapping
    public List<ApplicationStatusResponse> getAll(){return applicationStatusService.getAll();}

    @GetMapping("/{code}")
    public ApplicationStatusResponse getByCode(@PathVariable String code){return applicationStatusService.getByCode(code);}
}
