package com.company.insurance.application_service.controller;


import com.company.insurance.application_service.dto.ApplicationCreateRequest;
import com.company.insurance.application_service.dto.ApplicationDecisionRequest;
import com.company.insurance.application_service.dto.ApplicationResponse;
import com.company.insurance.application_service.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createApplication(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApplicationResponse> approve(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDecisionRequest request) {
        return ResponseEntity.ok(applicationService.approveApplication(id, request));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> reject(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDecisionRequest request) {
        return ResponseEntity.ok(applicationService.rejectApplication(id, request));
    }
}
