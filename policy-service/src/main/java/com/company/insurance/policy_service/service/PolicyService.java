package com.company.insurance.policy_service.service;

import com.company.insurance.policy_service.client.ApplicationServiceClient;
import com.company.insurance.policy_service.dto.ApplicationDto;
import com.company.insurance.policy_service.dto.PolicyCreateRequest;
import com.company.insurance.policy_service.dto.PolicyResponse;
import com.company.insurance.policy_service.dto.ProductSalesCountDto;
import com.company.insurance.policy_service.entity.Policy;
import com.company.insurance.policy_service.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final ApplicationServiceClient applicationServiceClient;

    public PolicyService(PolicyRepository policyRepository, ApplicationServiceClient applicationServiceClient){
        this.policyRepository = policyRepository;
        this.applicationServiceClient = applicationServiceClient;
    }

    public PolicyResponse createPolicy(PolicyCreateRequest request){
        return policyRepository.findByApplicationId(request.applicationId()).map(this::toResponse)
                .orElseGet(() -> {
                    ApplicationDto application = applicationServiceClient.getApplicationById(request.applicationId());
                    LocalDate startDate = LocalDate.now();
                    LocalDate endDate = startDate.plusYears(1);
                    Policy policy = new Policy();
                    policy.setApplicationId(request.applicationId());
                    policy.setCustomerId(application.customerId());
                    policy.setProductId(application.productId());
                    policy.setPremiumAmount(request.premiumAmount());
                    policy.setCurrencyCode(request.currencyCode());
                    policy.setStartDate(startDate);
                    policy.setEndDate(endDate);
                    policy.setStatus("ACTIVE");
                    policy.setIssuedAt(LocalDateTime.now());
                    policy.setPolicyNumber("PENDING");

                    Policy savedPolicy = policyRepository.save(policy);
                    savedPolicy.setPolicyNumber("POL-" + savedPolicy.getId());
                    Policy updatedPolicy = policyRepository.save(savedPolicy);

                    return toResponse(updatedPolicy);
                });
    }

    @Transactional(readOnly = true)
    public PolicyResponse getByApplicationId(Long applicationId) {
        return policyRepository.findByApplicationId(applicationId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Poliçe bulunamadı. Application ID: " + applicationId));
    }

    private PolicyResponse toResponse(Policy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getApplicationId(),
                policy.getCustomerId(),
                policy.getProductId(),
                policy.getPolicyNumber(),
                policy.getPremiumAmount(),
                policy.getCurrencyCode(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus(),
                policy.getIssuedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductSalesCountDto> getSalesSummary(LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(LocalTime.MAX);
        return policyRepository.countPoliciesGroupedByProduct(startDateTime, endDateTime);
    }
}
