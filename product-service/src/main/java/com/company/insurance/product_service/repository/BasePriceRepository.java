package com.company.insurance.product_service.repository;

import com.company.insurance.product_service.entity.BasePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasePriceRepository extends JpaRepository<BasePrice, Long> {
    List<BasePrice> findAllByIsDeletedFalse();

    Optional<BasePrice> findByPriceIdAndIsDeletedFalse(Long priceId);

    Optional<BasePrice> findByProductIdAndMonthAndYearAndIsDeletedFalse(Long productId, Integer month, Integer year);
}
