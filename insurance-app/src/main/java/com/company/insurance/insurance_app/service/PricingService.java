package com.company.insurance.insurance_app.service;


import com.company.insurance.insurance_app.client.ParameterServiceClient;
import com.company.insurance.insurance_app.client.ProductServiceClient;
import com.company.insurance.insurance_app.dto.BasePriceResponse;
import com.company.insurance.insurance_app.dto.ExchangeRateResponse;
import com.company.insurance.insurance_app.dto.PriceCalculationRequest;
import com.company.insurance.insurance_app.dto.PriceCalculationResponse;
import com.company.insurance.insurance_app.entity.*;
import com.company.insurance.insurance_app.exception.CustomerNotFoundException;
import com.company.insurance.insurance_app.exception.PricingFactorNotFoundException;
import com.company.insurance.insurance_app.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PricingService {
    private final ProductServiceClient productServiceClient;
    private final ParameterServiceClient parameterServiceClient;
    private final PricingFactorCacheService cacheService;
    private final CustomerRepository customerRepository;
    private final JobRepository jobRepository;
    private final HealthRepository healthRepository;
    private final HealthDiseaseRepository healthDiseaseRepository;
    private final DiseaseRepository diseaseRepository;

    public PricingService(ProductServiceClient productServiceClient, ParameterServiceClient parameterServiceClient,
                          PricingFactorCacheService cacheService, CustomerRepository customerRepository,
                          JobRepository jobRepository, HealthRepository healthRepository,
                          HealthDiseaseRepository healthDiseaseRepository, DiseaseRepository diseaseRepository){
        this.productServiceClient =productServiceClient;
        this.parameterServiceClient = parameterServiceClient;
        this.cacheService = cacheService;
        this.customerRepository = customerRepository;
        this.jobRepository = jobRepository;
        this.healthRepository = healthRepository;
        this.healthDiseaseRepository = healthDiseaseRepository;
        this.diseaseRepository = diseaseRepository;
    }

    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request){
        BasePriceResponse basePriceData = productServiceClient.getBasePrice(
                request.productId(),
                request.month(),
                request.year()
        );

        if (basePriceData == null || basePriceData.basePrice() == null){
            throw new IllegalArgumentException("İlgili döneme ve ürüne ait baz fiyat bulunamadı.");
        }

        BigDecimal originalBasePrice = basePriceData.basePrice();
        String originalCurrency = basePriceData.currencyCode();

        BigDecimal basePriceInTry = originalBasePrice;
        if(originalCurrency != null && !"TRY".equalsIgnoreCase(originalCurrency)){
            ExchangeRateResponse rateResponse = parameterServiceClient.getExchangeRate(originalCurrency);
            if (rateResponse == null || rateResponse.forexSelling() == null){
                throw new PricingFactorNotFoundException(originalCurrency + " için kur bilgisi parameter-service üzerinde bulunamadı!");
            }
            basePriceInTry = originalBasePrice.multiply(rateResponse.forexSelling());
        }

        Customer customer = customerRepository.findByCustomerIdAndIsDeletedFalse(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

        BigDecimal jobMultiplier = calculateJobMultiplier(customer.getJobId());
        BigDecimal ageMultiplier = calculateAgeMultiplier(customer.getBirthDate());

        Optional<Health> healthOpt = healthRepository.findFirstByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customer.getCustomerId());

        BigDecimal bmiMultiplier = BigDecimal.ONE;
        BigDecimal smokingMultiplier = BigDecimal.ONE;
        BigDecimal diseaseMultiplier = BigDecimal.ONE;

        if (healthOpt.isPresent()){
            Health health = healthOpt.get();
            bmiMultiplier = calculateBmiMultiplier(health.getHeight(), health.getWeight());
            if (health.isSmoking()){
                smokingMultiplier = cacheService.getCoefficient("SMOKING")
                        .orElseThrow(() -> new PricingFactorNotFoundException("SMOKING çarpanı parameter-service üzerinde tanımlı değil!"));
            }
            diseaseMultiplier = calculateDiseaseMultiplier(health.getHealthId());
        }

        BigDecimal finalPrice = basePriceInTry
                .multiply(jobMultiplier)
                .multiply(ageMultiplier)
                .multiply(bmiMultiplier)
                .multiply(smokingMultiplier)
                .multiply(diseaseMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> multipliers = new LinkedHashMap<>();
        multipliers.put("jobMultiplier", jobMultiplier);
        multipliers.put("ageMultiplier", ageMultiplier);
        multipliers.put("bmiMultiplier", bmiMultiplier);
        multipliers.put("smokingMultiplier", smokingMultiplier);
        multipliers.put("diseaseMultiplier", diseaseMultiplier);

        return new PriceCalculationResponse(customer.getCustomerId(), request.productId(), basePriceInTry, finalPrice,
                "TRY", multipliers);
    }

    private BigDecimal calculateJobMultiplier(Long jobId){
        if (jobId == null){
            return cacheService.getCoefficient("JOB_UNEMPLOYED")
                    .orElseThrow(() -> new PricingFactorNotFoundException(
                            "JOB_UNEMPLOYED çarpanı parameter-service üzerinde tanımlı değil!"));
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Belirtilen meslek bulunamadı: " + jobId));

        return cacheService.getMultiplierForRange("JOB_RISK", BigDecimal.valueOf(job.getRisk()))
                .orElseThrow(() -> new PricingFactorNotFoundException(
                        "Risk seviyesi " + job.getRisk() + " olan meslek için JOB_RISK çarpanı tanımlı değil!"));
    }

    BigDecimal calculateAgeMultiplier(LocalDate birthDate){
        if (birthDate == null) return BigDecimal.ONE;
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        return cacheService.getMultiplierForRange("AGE", BigDecimal.valueOf(age))
                .orElseThrow(() -> new PricingFactorNotFoundException(
                        age + " yaşı için geçerli bir AGE çarpanı tanımlı değil!"));
    }

    BigDecimal calculateBmiMultiplier(BigDecimal heightCm, BigDecimal weightKg){
        if (heightCm == null || weightKg == null || heightCm.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ONE;
        }

        double heightInMeters = heightCm.doubleValue() / 100.0;
        double bmi = weightKg.doubleValue() / (heightInMeters * heightInMeters);

        return cacheService.getMultiplierForRange("BMI", BigDecimal.valueOf(bmi))
                .orElseThrow(() -> new PricingFactorNotFoundException(
                        String.format("%.2f BMI değeri için çarpan bulunamadı!", bmi)));
    }

    private BigDecimal calculateDiseaseMultiplier(Long healthId){
        List<HealthDisease> healthDiseaseList = healthDiseaseRepository.findAllByHealthInfoIdAndIsDeletedFalse(healthId);
        if (healthDiseaseList.isEmpty()){
            return BigDecimal.ONE;
        }

        Set<Long> diseaseIds = healthDiseaseList.stream()
                .map(HealthDisease::getDiseaseId)
                .collect(Collectors.toSet());

        int totalSeverity = diseaseRepository.findAllById(diseaseIds).stream()
                .mapToInt(Disease::getSeverityScore)
                .sum();

        BigDecimal coefficient = cacheService.getCoefficient("DISEASE_SEVERITY_COEFFICIENT")
                .orElseThrow(() -> new PricingFactorNotFoundException(
                        "DISEASE_SEVERITY_COEFFICIENT katsayısı parameter-service üzerinde tanımlı değil!"));
        return BigDecimal.ONE.add(BigDecimal.valueOf(totalSeverity).multiply(coefficient));
    }
}
