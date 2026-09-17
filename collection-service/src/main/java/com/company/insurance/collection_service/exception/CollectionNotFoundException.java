package com.company.insurance.collection_service.exception;

public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(Long id) {
        super("Tahsilat bulunamadı: " + id);
    }
}
