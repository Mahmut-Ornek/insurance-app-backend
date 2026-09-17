package com.company.insurance.insurance_app.controller;


import com.company.insurance.insurance_app.dto.JobCreateRequest;
import com.company.insurance.insurance_app.dto.JobResponse;
import com.company.insurance.insurance_app.dto.JobUpdateRequest;
import com.company.insurance.insurance_app.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService){this.jobService = jobService;}

    @GetMapping
    public List<JobResponse> getAll(){return jobService.getAll();}

    @GetMapping("/{id}")
    public JobResponse getById(@PathVariable Long id){return jobService.getById(id);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@Valid @RequestBody JobCreateRequest request){
        return jobService.create(request);
    }

    @PutMapping("/{id}")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody JobUpdateRequest request){
        return jobService.update(id, request);
    }
}
