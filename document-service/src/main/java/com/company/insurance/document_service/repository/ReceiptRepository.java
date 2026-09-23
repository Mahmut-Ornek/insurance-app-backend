package com.company.insurance.document_service.repository;

import com.company.insurance.document_service.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByCollectionPaymentId(Long collectionPaymentId);
}