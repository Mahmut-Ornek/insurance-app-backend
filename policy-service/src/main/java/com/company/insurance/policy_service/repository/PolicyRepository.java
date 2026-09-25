package com.company.insurance.policy_service.repository;

import com.company.insurance.policy_service.dto.ProductSalesCountDto;
import com.company.insurance.policy_service.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByApplicationId(Long applicationId);

    @Query("""
    SELECT new com.company.insurance.policy_service.dto.ProductSalesCountDto(p.productId, COUNT(p.id))
    FROM Policy p
    WHERE p.issuedAt >= :from AND p.issuedAt <= :to
    GROUP BY p.productId
""")
    List<ProductSalesCountDto> countPoliciesGroupedByProduct(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
