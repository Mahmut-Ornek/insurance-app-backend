package com.company.insurance.parameter_service.repository;

import com.company.insurance.parameter_service.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}
