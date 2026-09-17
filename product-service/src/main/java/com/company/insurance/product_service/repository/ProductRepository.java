package com.company.insurance.product_service.repository;

import com.company.insurance.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByIsDeletedFalse();

    Optional<Product> findByProductIdAndIsDeletedFalse(Long productId);
}
