package com.company.insurance.insurance_app.controller;


import com.company.insurance.insurance_app.dto.*;
import com.company.insurance.insurance_app.service.DiseaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diseases")
public class DiseaseController {
    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService){this.diseaseService = diseaseService;}

    @GetMapping
    public List<DiseaseResponse> getAll(){return diseaseService.getAll();}

    @GetMapping("/{id}")
    public DiseaseResponse getById(@PathVariable Long id){return diseaseService.getById(id);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiseaseResponse create(@Valid @RequestBody DiseaseRequest request){
        return diseaseService.create(request);
    }

    @PutMapping("/{id}")
    public DiseaseResponse update(@PathVariable Long id, @Valid @RequestBody DiseaseRequest request) {
        return diseaseService.update(id, request);
    }
}
