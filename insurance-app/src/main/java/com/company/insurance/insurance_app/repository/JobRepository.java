package com.company.insurance.insurance_app.repository;

import com.company.insurance.insurance_app.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByName(String name);
}
