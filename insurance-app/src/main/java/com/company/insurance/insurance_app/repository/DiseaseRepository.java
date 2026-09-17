package com.company.insurance.insurance_app.repository;

import com.company.insurance.insurance_app.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    boolean existsByCode(String code);

}
