package com.company.insurance.collection_service.repository;

import com.company.insurance.collection_service.entity.CollectionPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionPaymentRepository extends JpaRepository<CollectionPayment, Long> {
    List<CollectionPayment> findAllByCollectionId(Long collectionId);
    boolean existsByPaymentReference(String paymentReference);
}
