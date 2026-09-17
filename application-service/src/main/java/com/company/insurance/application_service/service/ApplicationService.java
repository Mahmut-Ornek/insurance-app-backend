package com.company.insurance.application_service.service;


import com.company.insurance.application_service.client.CollectionServiceClient;
import com.company.insurance.application_service.client.InsuranceAppClient;
import com.company.insurance.application_service.dto.*;
import com.company.insurance.application_service.entity.Application;
import com.company.insurance.application_service.enums.ApplicationStatus;
import com.company.insurance.application_service.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final InsuranceAppClient insuranceAppClient;
    private final CollectionServiceClient collectionServiceClient;

    public ApplicationService(ApplicationRepository applicationRepository, InsuranceAppClient insuranceAppClient,
                              CollectionServiceClient collectionServiceClient){
        this.applicationRepository = applicationRepository;
        this.insuranceAppClient = insuranceAppClient;
        this.collectionServiceClient = collectionServiceClient;
    }

    private ApplicationResponse toResponse(Application app){
        return new ApplicationResponse(app.getApplicationId(), app.getCustomerId(), app.getProductId(), app.getStatus(),
                app.getCalculatedPrice(), app.getCurrencyCode(), app.getDescription(), app.getAppliedAt(),
                app.getDecidedAt(), app.getDecidedBy());
    }

    public ApplicationResponse getById(Long id){
        return toResponse(getActiveApplication(id));
    }

    private Application getActiveApplication(Long id){
        return applicationRepository.findByApplicationIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Başvuru bulunamadı: " + id));
    }

    @Transactional
    public ApplicationResponse createApplication(ApplicationCreateRequest request){
        boolean hasPending = applicationRepository.existsByCustomerIdAndProductIdAndStatusAndIsDeletedFalse(
                request.customerId(), request.productId(), ApplicationStatus.PENDING);
        if (hasPending){
            throw new IllegalStateException("Müşterinin bu ürün için zaten değerlendirme aşamasında açık bir başvurusu bulunmaktadır!");
        }

        PriceCalculationRequest pricingReq = new PriceCalculationRequest(request.customerId(), request.productId(),
                request.month(), request.year());
        PriceCalculationResponse priceResponse = insuranceAppClient.calculatePrice(pricingReq);

        Application application = new Application();
        application.setCustomerId(request.customerId());
        application.setProductId(request.productId());
        application.setStatus(ApplicationStatus.PENDING);
        application.setCalculatedPrice(priceResponse.finalPrice());
        application.setCurrencyCode(priceResponse.currencyCode());
        application.setAppliedAt(LocalDateTime.now());
        Application saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponse approveApplication(Long id, ApplicationDecisionRequest request){
        Application application = getActiveApplication(id);

        if(application.getStatus() != ApplicationStatus.PENDING){
            throw new IllegalStateException("Sadece PENDING durumundaki başvurular onaylanabilir. Mevcut durum: " + application.getStatus());
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setDecidedBy(request.decidedBy());
        application.setDecidedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);

        CollectionCreateRequest collectionRequest = new CollectionCreateRequest(saved.getApplicationId(), saved.getCalculatedPrice(), saved.getCurrencyCode());
        collectionServiceClient.createCollection(collectionRequest);

        return toResponse(saved);
    }

    @Transactional
    public ApplicationResponse rejectApplication(Long id, ApplicationDecisionRequest request){
        Application application = getActiveApplication(id);
        if(application.getStatus() != ApplicationStatus.PENDING){
            throw new IllegalStateException("Sadece PENDING durumundaki başvurular reddedilebilir. Mevcut durum: " + application.getStatus());
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setDecidedBy(request.decidedBy());
        application.setDescription(request.description());
        application.setDecidedAt(LocalDateTime.now());

        return toResponse(applicationRepository.save(application));
    }
}
