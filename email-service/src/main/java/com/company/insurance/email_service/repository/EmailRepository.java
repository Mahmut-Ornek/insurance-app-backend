package com.company.insurance.email_service.repository;

import com.company.insurance.email_service.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
