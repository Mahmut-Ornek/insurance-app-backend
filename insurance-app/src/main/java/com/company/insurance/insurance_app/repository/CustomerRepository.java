package com.company.insurance.insurance_app.repository;

import java.util.List;
import java.util.Optional;

import com.company.insurance.insurance_app.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByIsDeletedFalse();

    Optional<Customer> findByCustomerIdAndIsDeletedFalse(Long customerId);

    boolean existsByGovernmentId(String governmentId);
}
