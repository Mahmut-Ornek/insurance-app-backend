package com.company.insurance.insurance_app.service;

import com.company.insurance.insurance_app.dto.DiseaseRequest;
import com.company.insurance.insurance_app.dto.DiseaseResponse;
import com.company.insurance.insurance_app.entity.Disease;
import com.company.insurance.insurance_app.exception.DiseaseNotFoundException;
import com.company.insurance.insurance_app.repository.DiseaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiseaseService {
    private final DiseaseRepository diseaseRepository;

    public DiseaseService(DiseaseRepository diseaseRepository){this.diseaseRepository = diseaseRepository;}

    private DiseaseResponse toResponse(Disease disease){
        return new DiseaseResponse(disease.getDiseaseId(), disease.getCode(), disease.getName(), disease.getSeverityScore(),
                disease.getDescription(), disease.getUpdatedBy(), disease.getUpdateDate());
    }

    public List<DiseaseResponse> getAll(){
        return diseaseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DiseaseResponse getById(Long id){
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new DiseaseNotFoundException(id));

        return toResponse(disease);
    }

    public DiseaseResponse create(DiseaseRequest request){
        if (diseaseRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Bu hastalık kodu zaten kayıtlı: " + request.code());
        }

        Disease disease = new Disease();
        disease.setCode(request.code());
        disease.setName(request.name());
        disease.setSeverityScore(request.severityScore());
        disease.setDescription(request.description());

        Disease saved = diseaseRepository.save(disease);
        return toResponse(saved);
    }

    public DiseaseResponse update(Long id, DiseaseRequest request){
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new DiseaseNotFoundException(id));

        disease.setName(request.name());
        disease.setSeverityScore(request.severityScore());
        disease.setDescription(request.description());
        disease.setUpdatedBy(request.updatedBy());
        disease.setUpdateDate(LocalDateTime.now());

        Disease saved = diseaseRepository.save(disease);
        return toResponse(saved);
    }
}
