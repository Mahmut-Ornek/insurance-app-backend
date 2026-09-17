package com.company.insurance.application_service.repository;

import com.company.insurance.application_service.entity.Application;
import com.company.insurance.application_service.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByCustomerIdAndProductIdAndStatusAndIsDeletedFalse(Long customerId, Long productId, ApplicationStatus status);
    Optional<Application> findByApplicationIdAndIsDeletedFalse(Long applicationId);
    List<Application> findAllByCustomerIdAndIsDeletedFalse(Long customerId);
}
