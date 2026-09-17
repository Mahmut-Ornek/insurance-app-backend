package com.company.insurance.parameter_service.service;


import com.company.insurance.parameter_service.dto.PricingFactorResponse;
import com.company.insurance.parameter_service.dto.UpdatePricingFactorRequest;
import com.company.insurance.parameter_service.entity.PricingFactor;
import com.company.insurance.parameter_service.repository.PricingFactorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PricingFactorService {
    private final PricingFactorRepository pricingFactorRepository;

    public PricingFactorService(PricingFactorRepository pricingFactorRepository){this.pricingFactorRepository = pricingFactorRepository;}

    private PricingFactorResponse toResponse(PricingFactor factor){
        return new PricingFactorResponse(
                factor.getId(),
                factor.getFactorType(),
                factor.getMinValue(),
                factor.getMaxValue(),
                factor.getMultiplier(),
                factor.getDescription()
        );
    }

    public List<PricingFactorResponse> getAll(){
        return pricingFactorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public PricingFactorResponse updateMultiplier(Long id, UpdatePricingFactorRequest request){
        PricingFactor factor = pricingFactorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Belirtilen ID'ye sahip çarpan bulunamadı: " + id));

        factor.setMultiplier(request.multiplier());
        if (request.description() != null){
            factor.setDescription(request.description());
        }

        PricingFactor saved = pricingFactorRepository.save(factor);
        return toResponse(saved);
    }
}
