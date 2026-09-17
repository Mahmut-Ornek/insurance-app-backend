package com.company.insurance.insurance_app.repository;

import com.company.insurance.insurance_app.entity.HealthDisease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface HealthDiseaseRepository extends JpaRepository<HealthDisease, Long> {
    List<HealthDisease> findAllByHealthInfoIdAndIsDeletedFalse(Long healthInfoId);
    List<HealthDisease> findAllByHealthInfoIdInAndIsDeletedFalse(Collection<Long> healthInfoIds);
}