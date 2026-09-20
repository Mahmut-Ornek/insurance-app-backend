package com.company.insurance.document_service.repository;

import com.company.insurance.document_service.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    boolean existsByCollectionPaymentId(Long collectionPaymentId);

    @Query("SELECT r.collectionPaymentId FROM Receipt r")
    Set<Long> findAllExistingPaymentIds();
}