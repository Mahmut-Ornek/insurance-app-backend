package com.company.insurance.parameter_service.service;


import com.company.insurance.parameter_service.dto.ApplicationStatusResponse;
import com.company.insurance.parameter_service.entity.ApplicationStatus;
import com.company.insurance.parameter_service.exception.ApplicationStatusNotFoundException;
import com.company.insurance.parameter_service.repository.ApplicationStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationStatusService {
    private final ApplicationStatusRepository applicationStatusRepository;

    public ApplicationStatusService(ApplicationStatusRepository applicationStatusRepository){
        this.applicationStatusRepository =applicationStatusRepository;
    }

    private ApplicationStatusResponse toResponse(ApplicationStatus applicationStatus){
        return new ApplicationStatusResponse(applicationStatus.getCode(), applicationStatus.getName(), applicationStatus.getDescription());
    }

    public List<ApplicationStatusResponse> getAll(){
        List<ApplicationStatusResponse> responses = applicationStatusRepository.findAll().stream().map(this::toResponse).toList();
        return responses;
    }

    public ApplicationStatusResponse getByCode(String code){
        ApplicationStatus applicationStatus = applicationStatusRepository.findById(code)
                .orElseThrow(() -> new ApplicationStatusNotFoundException(code));

        return toResponse(applicationStatus);
    }
}
