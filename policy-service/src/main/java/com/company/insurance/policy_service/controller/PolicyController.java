package com.company.insurance.policy_service.controller;

import com.company.insurance.policy_service.dto.PolicyCreateRequest;
import com.company.insurance.policy_service.dto.PolicyResponse;
import com.company.insurance.policy_service.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/policies")
public class PolicyController {
    private final PolicyService policyService;
    public PolicyController(PolicyService policyService){this.policyService = policyService;}

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyCreateRequest request) {
        return ResponseEntity.ok(policyService.createPolicy(request));
    }

    @GetMapping("/by-application/{applicationId}")
    public ResponseEntity<PolicyResponse> getByApplicationId(@PathVariable Long applicationId) {
        return ResponseEntity.ok(policyService.getByApplicationId(applicationId));
    }
}
