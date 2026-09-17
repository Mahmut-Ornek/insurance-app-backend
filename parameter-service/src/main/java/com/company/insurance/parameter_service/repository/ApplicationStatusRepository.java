package com.company.insurance.parameter_service.repository;

import com.company.insurance.parameter_service.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, String> {
}
