package com.company.insurance.insurance_app.service;

import com.company.insurance.insurance_app.dto.CustomerCreateRequest;
import com.company.insurance.insurance_app.dto.CustomerUpdateRequest;
import com.company.insurance.insurance_app.entity.Customer;
import com.company.insurance.insurance_app.entity.Job;
import com.company.insurance.insurance_app.exception.CustomerNotFoundException;
import com.company.insurance.insurance_app.repository.CustomerRepository;
import com.company.insurance.insurance_app.dto.CustomerResponse;
import com.company.insurance.insurance_app.repository.JobRepository;
import com.company.insurance.insurance_app.validation.GovernmentIdValidator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final JobRepository jobRepository;

    public CustomerService(CustomerRepository customerRepository, JobRepository jobRepository){
        this.jobRepository = jobRepository;
        this.customerRepository = customerRepository;
    }

    private CustomerResponse toResponse(Customer customer, String jobName){
        return new CustomerResponse(customer.getCustomerId(),
                customer.getName(),
                customer.getSurname(),
                customer.getGovernmentId(),
                customer.getEmail(),
                customer.getJobId(),
                jobName,
                customer.getMotherName(),
                customer.getFatherName(),
                customer.getBirthDate(),
                customer.getAddress(),
                customer.getUsername(),
                customer.isDeleted(),
                customer.getCreatedBy());
    }

    public List<CustomerResponse> getAll(){
        List<Customer> customers = customerRepository.findAllByIsDeletedFalse();

        if (customers.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> jobIds = customers.stream()
                .map(Customer::getJobId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> jobNames = jobRepository.findAllById(jobIds).stream()
                .collect(Collectors.toMap(Job::getJobId, Job::getName));

        return customers.stream()
                .map(customer -> toResponse(customer, jobNames.get(customer.getJobId())))
                .collect(Collectors.toList());
    }

    public CustomerResponse getById(Long id){
        Customer customer = customerRepository.findByCustomerIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        String jobName = null;
        if(customer.getJobId() != null){
            jobName = jobRepository.findById(customer.getJobId())
                    .map(Job::getName)
                    .orElse(null);
        }

        return toResponse(customer, jobName);
    }

    public CustomerResponse create(CustomerCreateRequest request){
        if (!GovernmentIdValidator.isValid(request.governmentId())){
            throw new IllegalArgumentException("Geçersiz TCKN!!");
        }

        if (customerRepository.existsByGovernmentId(request.governmentId())){
            throw new IllegalArgumentException("Bu TCKN zaten kayıtlı!!!");
        }

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setSurname(request.surname());
        customer.setGovernmentId(request.governmentId());
        customer.setEmail(request.email());
        customer.setJobId(request.jobId());
        customer.setMotherName(request.motherName());
        customer.setFatherName(request.fatherName());
        customer.setBirthDate(request.birthDate());
        customer.setAddress(request.address());
        customer.setUsername(request.username());

        Customer saved = customerRepository.save(customer);

        String jobName = null;
        if(customer.getJobId() != null){
            jobName = jobRepository.findById(customer.getJobId())
                    .map(Job::getName)
                    .orElse(null);
        }
        return toResponse(saved, jobName);
    }

    public CustomerResponse update(Long id, CustomerUpdateRequest request){
        Customer customer = customerRepository.findByCustomerIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setName(request.name());
        customer.setSurname(request.surname());
        customer.setEmail(request.email());
        customer.setJobId(request.jobId());
        customer.setMotherName(request.motherName());
        customer.setFatherName(request.fatherName());
        customer.setBirthDate(request.birthDate());
        customer.setAddress(request.address());
        customer.setUsername(request.username());

        Customer saved = customerRepository.save(customer);

        String jobName = null;
        if(customer.getJobId() != null){
            jobName = jobRepository.findById(customer.getJobId())
                    .map(Job::getName)
                    .orElse(null);
        }
        return toResponse(saved, jobName);
    }

    public void delete(Long id){
        Customer customer = customerRepository.findByCustomerIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }
}
