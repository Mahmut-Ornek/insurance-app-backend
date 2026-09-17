package com.company.insurance.insurance_app.service;


import com.company.insurance.insurance_app.dto.JobCreateRequest;
import com.company.insurance.insurance_app.dto.JobResponse;
import com.company.insurance.insurance_app.dto.JobUpdateRequest;
import com.company.insurance.insurance_app.entity.Job;
import com.company.insurance.insurance_app.exception.JobNotFoundException;
import com.company.insurance.insurance_app.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository){this.jobRepository = jobRepository;}

    private JobResponse toResponse(Job job){
        return new JobResponse(job.getJobId(), job.getName(), job.getRisk());
    }

    public List<JobResponse> getAll(){
        List<Job> jobs = jobRepository.findAll();
        List<JobResponse> responses = new ArrayList<>();
        for(Job job : jobs){
            responses.add(toResponse(job));
        }
        return responses;
    }

    public JobResponse getById(Long id){
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        return toResponse(job);
    }

    public JobResponse create(JobCreateRequest request){
        if (jobRepository.existsByName(request.name())){
            throw new IllegalArgumentException("Bu isimde bir iş zaten kayıtlı: " + request.name());
        }

        Job job = new Job();
        job.setName(request.name());
        job.setRisk(request.risk());

        Job saved = jobRepository.save(job);
        return toResponse(saved);
    }

    public JobResponse update(Long id, JobUpdateRequest request){
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        job.setRisk(request.risk());
        Job saved = jobRepository.save(job);
        return toResponse(saved);
    }
}
