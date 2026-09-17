package com.company.insurance.insurance_app.service;

import com.company.insurance.insurance_app.dto.*;
import com.company.insurance.insurance_app.entity.Health;
import com.company.insurance.insurance_app.entity.HealthDisease;
import com.company.insurance.insurance_app.exception.HealthInfoNotFoundException;
import com.company.insurance.insurance_app.repository.HealthDiseaseRepository;
import com.company.insurance.insurance_app.repository.HealthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HealthService {

    private final HealthRepository healthRepository;
    private final HealthDiseaseRepository healthDiseaseRepository;

    public HealthService(HealthRepository healthRepository,
                         HealthDiseaseRepository healthDiseaseRepository) {
        this.healthRepository = healthRepository;
        this.healthDiseaseRepository = healthDiseaseRepository;
    }

    void validateChronicDiseaseConsistency(boolean hasChronicDisease, List<HealthDiseaseItemDto> diseases) {
        boolean hasDiseases = diseases != null && !diseases.isEmpty();
        if (hasChronicDisease && !hasDiseases) {
            throw new IllegalArgumentException("Kronik hastalık belirtildiğinde en az bir hastalık detayı girilmelidir.");
        }
        if (!hasChronicDisease && hasDiseases) {
            throw new IllegalArgumentException("Kronik hastalık bulunmadığı belirtilmişken hastalık listesi gönderilemez.");
        }

        if (hasDiseases) {
            long distinctCount = diseases.stream()
                    .map(HealthDiseaseItemDto::diseaseId)
                    .distinct()
                    .count();

            if (distinctCount != diseases.size()) {
                throw new IllegalArgumentException("Aynı hastalık listede birden fazla kez yer alamaz.");
            }
        }
    }

    private String resolveAndValidateSurgeryDetails(boolean hasPastSurgeries, String surgeryDetails) {
        String cleaned = (surgeryDetails != null && !surgeryDetails.isBlank())
                ? surgeryDetails.trim()
                : null;

        if (hasPastSurgeries && cleaned == null) {
            throw new IllegalArgumentException("Geçmiş ameliyat belirtildiğinde ameliyat detayı girilmelidir.");
        }
        if (!hasPastSurgeries && cleaned != null) {
            throw new IllegalArgumentException("Geçmiş ameliyat bulunmadığında ameliyat detayı boş olmalıdır.");
        }

        return cleaned;
    }

    private HealthResponse toResponse(Health health, List<HealthDisease> diseases) {
        List<HealthDiseaseItemDto> diseaseDtos = diseases.stream()
                .filter(d -> !d.isDeleted())
                .map(d -> new HealthDiseaseItemDto(d.getDiseaseId(), d.getDiagnosisDate(), d.getDetails()))
                .toList();

        return new HealthResponse(
                health.getHealthId(),
                health.getCustomerId(),
                health.isSmoking(),
                health.getSmokingFrequency(),
                health.getHeight(),
                health.getWeight(),
                health.getBloodType(),
                health.getAlcoholConsumption(),
                health.isHasChronicDisease(),
                health.isHasPastSurgeries(),
                health.getSurgeryDetails(),
                health.getCreatedAt(),
                health.getCreatedBy(),
                health.getUpdatedAt(),
                health.getUpdatedBy(),
                health.isDeleted(),
                diseaseDtos
        );
    }

    @Transactional(readOnly = true)
    public List<HealthResponse> getAll() {
        List<Health> healthList = healthRepository.findAllByIsDeletedFalse();
        if (healthList.isEmpty()) {
            return Collections.emptyList();
        }

        // N+1 önleme: Tüm ID'ler tek sorguda çekilir (Batch-fetch)
        List<Long> healthIds = healthList.stream().map(Health::getHealthId).toList();
        List<HealthDisease> allDiseases = healthDiseaseRepository.findAllByHealthInfoIdInAndIsDeletedFalse(healthIds);

        Map<Long, List<HealthDisease>> diseasesByHealthId = allDiseases.stream()
                .collect(Collectors.groupingBy(HealthDisease::getHealthInfoId));

        return healthList.stream()
                .map(h -> toResponse(h, diseasesByHealthId.getOrDefault(h.getHealthId(), Collections.emptyList())))
                .toList();
    }

    @Transactional(readOnly = true)
    public HealthResponse getById(Long id) {
        Health health = healthRepository.findByHealthIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new HealthInfoNotFoundException(id));
        List<HealthDisease> diseases = healthDiseaseRepository.findAllByHealthInfoIdAndIsDeletedFalse(id);
        return toResponse(health, diseases);
    }

    public HealthResponse create(HealthCreateRequest request) {
        validateChronicDiseaseConsistency(request.hasChronicDisease(), request.diseases());


        Health health = new Health();
        health.setCustomerId(request.customerId());
        health.setSmoking(request.isSmoking());
        health.setSmokingFrequency(request.smokingFrequency());
        health.setHeight(request.height());
        health.setWeight(request.weight());
        health.setBloodType(request.bloodType());
        health.setAlcoholConsumption(request.alcoholConsumption());
        health.setHasChronicDisease(request.hasChronicDisease());
        health.setHasPastSurgeries(request.hasPastSurgeries());
        health.setSurgeryDetails(resolveAndValidateSurgeryDetails(request.hasPastSurgeries(), request.surgeryDetails()));
        health.setCreatedBy(request.createdBy());

        Health savedHealth = healthRepository.save(health);

        List<HealthDisease> savedDiseases = new ArrayList<>();
        if (request.hasChronicDisease() && request.diseases() != null) {
            for (HealthDiseaseItemDto item : request.diseases()) {
                HealthDisease hd = new HealthDisease();
                hd.setHealthInfoId(savedHealth.getHealthId());
                hd.setDiseaseId(item.diseaseId());
                hd.setDiagnosisDate(item.diagnosisDate());
                hd.setDetails(item.details());
                savedDiseases.add(healthDiseaseRepository.save(hd));
            }
        }

        return toResponse(savedHealth, savedDiseases);
    }

    public HealthResponse update(Long id, HealthUpdateRequest request) {
        validateChronicDiseaseConsistency(request.hasChronicDisease(), request.diseases());

        Health health = healthRepository.findByHealthIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new HealthInfoNotFoundException(id));

        health.setSmoking(request.isSmoking());
        health.setSmokingFrequency(request.smokingFrequency());
        health.setHeight(request.height());
        health.setWeight(request.weight());
        health.setAlcoholConsumption(request.alcoholConsumption());
        health.setHasChronicDisease(request.hasChronicDisease());
        health.setHasPastSurgeries(request.hasPastSurgeries());
        health.setSurgeryDetails(resolveAndValidateSurgeryDetails(request.hasPastSurgeries(), request.surgeryDetails()));
        health.setUpdatedBy(request.updatedBy());

        Health savedHealth = healthRepository.save(health);

        // Mevcut hastalık kayıtlarını al
        List<HealthDisease> existingDiseases = healthDiseaseRepository.findAllByHealthInfoIdAndIsDeletedFalse(id);

        List<HealthDiseaseItemDto> newItems = request.diseases() != null ? request.diseases() : Collections.emptyList();

        // 1. Yeni listede yer almayan mevcut kayıtları soft-delete yap (is_deleted = true)
        Set<Long> incomingDiseaseIds = newItems.stream()
                .map(HealthDiseaseItemDto::diseaseId)
                .collect(Collectors.toSet());

        for (HealthDisease existing : existingDiseases) {
            if (!incomingDiseaseIds.contains(existing.getDiseaseId())) {
                existing.setDeleted(true);
                healthDiseaseRepository.save(existing);
            }
        }

        // 2. Yeni listedeki kayıtları güncelle veya ekle
        List<HealthDisease> finalActiveDiseases = new ArrayList<>();
        for (HealthDiseaseItemDto item : newItems) {
            Optional<HealthDisease> matchingExisting = existingDiseases.stream()
                    .filter(d -> d.getDiseaseId().equals(item.diseaseId()))
                    .findFirst();

            if (matchingExisting.isPresent()) {
                HealthDisease hd = matchingExisting.get();
                hd.setDiagnosisDate(item.diagnosisDate());
                hd.setDetails(item.details());
                finalActiveDiseases.add(healthDiseaseRepository.save(hd));
            } else {
                HealthDisease hd = new HealthDisease();
                hd.setHealthInfoId(savedHealth.getHealthId());
                hd.setDiseaseId(item.diseaseId());
                hd.setDiagnosisDate(item.diagnosisDate());
                hd.setDetails(item.details());
                finalActiveDiseases.add(healthDiseaseRepository.save(hd));
            }
        }

        return toResponse(savedHealth, finalActiveDiseases);
    }

    public void delete(Long id) {
        Health health = healthRepository.findByHealthIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new HealthInfoNotFoundException(id));
        health.setDeleted(true);
        healthRepository.save(health);

        // İlişkili hastalık kayıtlarını da soft-delete yap
        List<HealthDisease> diseases = healthDiseaseRepository.findAllByHealthInfoIdAndIsDeletedFalse(id);
        for (HealthDisease hd : diseases) {
            hd.setDeleted(true);
            healthDiseaseRepository.save(hd);
        }
    }
}