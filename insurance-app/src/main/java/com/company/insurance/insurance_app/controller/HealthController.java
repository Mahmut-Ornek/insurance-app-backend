package com.company.insurance.insurance_app.controller;


import com.company.insurance.insurance_app.dto.HealthCreateRequest;
import com.company.insurance.insurance_app.dto.HealthResponse;
import com.company.insurance.insurance_app.dto.HealthUpdateRequest;
import com.company.insurance.insurance_app.service.HealthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/healthinfo")
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService){this.healthService = healthService;}

    @GetMapping
    public List<HealthResponse> getAll(){return healthService.getAll();}

    @GetMapping("/{id}")
    public HealthResponse getById(@PathVariable Long id){return healthService.getById(id);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HealthResponse create(@Valid @RequestBody HealthCreateRequest request){
        return healthService.create(request);
    }

    @PutMapping("/{id}")
    public HealthResponse update(@PathVariable Long id, @Valid @RequestBody HealthUpdateRequest request){
        return healthService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){healthService.delete(id);}
}
