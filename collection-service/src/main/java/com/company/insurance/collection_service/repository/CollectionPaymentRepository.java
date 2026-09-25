package com.company.insurance.collection_service.repository;

import com.company.insurance.collection_service.entity.CollectionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CollectionPaymentRepository extends JpaRepository<CollectionPayment, Long> {
    List<CollectionPayment> findAllByCollectionId(Long collectionId);
    boolean existsByPaymentReference(String paymentReference);

    @Query("""
    SELECT COALESCE(SUM(cp.amount), 0)
    FROM CollectionPayment cp
    WHERE cp.paidAt >= :from AND cp.paidAt <= :to
    """)
    BigDecimal sumPaidAmountBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
