package com.company.insurance.collection_service.repository;

import com.company.insurance.collection_service.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByIsDeletedFalse();
    Optional<Collection> findByIdAndIsDeletedFalse(Long id);
}
