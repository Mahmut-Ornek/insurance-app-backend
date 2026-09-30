package com.company.insurance.parameter_service.service;


import com.company.insurance.parameter_service.dto.PricingFactorResponse;
import com.company.insurance.parameter_service.dto.UpdatePricingFactorRequest;
import com.company.insurance.parameter_service.entity.PricingFactor;
import com.company.insurance.parameter_service.repository.PricingFactorRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PricingFactorService {
    private static final String CACHE_KEY = "pricing-factors";
    private final RedisTemplate<String, List<PricingFactorResponse>> redisTemplate;
    private final PricingFactorRepository pricingFactorRepository;

    public PricingFactorService(PricingFactorRepository pricingFactorRepository, RedisTemplate<String, List<PricingFactorResponse>> redisTemplate){
        this.pricingFactorRepository = pricingFactorRepository;
        this.redisTemplate = redisTemplate;
    }

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
        List<PricingFactorResponse> cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null){
            return cached;
        }

        List<PricingFactorResponse> fresh = pricingFactorRepository.findAll().stream().map(this::toResponse).toList();
        redisTemplate.opsForValue().set(CACHE_KEY, fresh);
        return fresh;
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

        redisTemplate.delete(CACHE_KEY);

        return toResponse(saved);
    }
}
