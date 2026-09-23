package com.company.insurance.collection_service.service;


import com.company.insurance.collection_service.client.PolicyServiceClient;
import com.company.insurance.collection_service.dto.CollectionCreateRequest;
import com.company.insurance.collection_service.dto.CollectionPaymentItemDto;
import com.company.insurance.collection_service.dto.CollectionResponse;
import com.company.insurance.collection_service.dto.PaymentSummaryResponse;
import com.company.insurance.collection_service.entity.Collection;
import com.company.insurance.collection_service.entity.CollectionPayment;
import com.company.insurance.collection_service.exception.CollectionNotFoundException;
import com.company.insurance.collection_service.repository.CollectionPaymentRepository;
import com.company.insurance.collection_service.repository.CollectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CollectionService {
    private final PolicyServiceClient policyServiceClient;
    private final CollectionRepository collectionRepository;
    private final CollectionPaymentRepository paymentRepository;

    public CollectionService(PolicyServiceClient policyServiceClient, CollectionRepository collectionRepository, CollectionPaymentRepository paymentRepository){
        this.collectionRepository = collectionRepository;
        this.paymentRepository = paymentRepository;
        this.policyServiceClient = policyServiceClient;
    }

    private CollectionResponse toResponse(Collection collection){
        return new CollectionResponse(collection.getId(), collection.getApplicationId(), collection.getTotalAmount(),
                collection.getCollectedAmount(), collection.getCurrencyCode(), collection.isClosed(), collection.getOpenedAt(),
                collection.getClosedAt(), collection.isDeleted());
    }

    @Transactional(readOnly = true)
    public List<CollectionResponse> getAll() {
        return collectionRepository.findAllByIsDeletedFalse().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CollectionResponse getById(Long id){
        Collection collection = collectionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CollectionNotFoundException(id));
        return toResponse(collection);
    }

    public CollectionResponse create(CollectionCreateRequest request){
        Collection collection = new Collection();
        collection.setApplicationId(request.applicationId());
        collection.setTotalAmount(request.totalAmount());
        collection.setCollectedAmount(BigDecimal.ZERO);
        collection.setCurrencyCode(request.currencyCode());
        collection.setClosed(false);
        collection.setOpenedAt(LocalDateTime.now());
        Collection saved = collectionRepository.save(collection);
        return toResponse(saved);
    }

    public CollectionResponse recordPayment(Long collectionId, CollectionPaymentItemDto paymentDto){
        Collection collection = collectionRepository.findByIdAndIsDeletedFalse(collectionId)
                .orElseThrow(() -> new CollectionNotFoundException(collectionId));

        if (paymentRepository.existsByPaymentReference(paymentDto.paymentReference())) {
            return toResponse(collection);
        }

        if(collection.isClosed()){
            throw new IllegalStateException("Bu tahsilat zaten kapatılmıştır, yeni ödeme yapılamaz!");
        }

        CollectionPayment payment = new CollectionPayment();
        payment.setCollectionId(collection.getId());
        payment.setAmount(paymentDto.amount());
        payment.setPaidAt(paymentDto.paidAt() != null ? paymentDto.paidAt() : LocalDateTime.now());
        payment.setPaymentReference(paymentDto.paymentReference());
        paymentRepository.save(payment);

        BigDecimal newCollected = collection.getCollectedAmount().add(paymentDto.amount());
        collection.setCollectedAmount(newCollected);

        if (newCollected.compareTo(collection.getTotalAmount()) >= 0){
            collection.setClosed(true);
            collection.setClosedAt(LocalDateTime.now());

            // İZOLE TETİKLEME
            policyServiceClient.triggerPolicyCreation(
                    collection.getApplicationId(),
                    collection.getTotalAmount(),
                    collection.getCurrencyCode()
            );
        }
        Collection updated = collectionRepository.save(collection);
        return toResponse(updated);
    }

    public void delete(Long id){
        Collection collection = collectionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CollectionNotFoundException(id));
        collection.setDeleted(true);
        collectionRepository.save(collection);
    }

    @Transactional(readOnly = true)
    public List<PaymentSummaryResponse> getAllPayments() {
        List<CollectionPayment> payments = paymentRepository.findAll();
        if (payments.isEmpty()) {
            return List.of();
        }

        Set<Long> collectionIds = payments.stream()
                .map(CollectionPayment::getCollectionId)
                .collect(java.util.stream.Collectors.toSet());

        java.util.Map<Long, Collection> collectionsById = collectionRepository.findAllById(collectionIds).stream()
                .collect(java.util.stream.Collectors.toMap(Collection::getId, c -> c));

        return payments.stream()
                .map(payment -> {
                    Collection collection = collectionsById.get(payment.getCollectionId());
                    if (collection == null) {
                        throw new CollectionNotFoundException(payment.getCollectionId());
                    }

                    return new PaymentSummaryResponse(
                            payment.getId(),
                            payment.getCollectionId(),
                            collection.getApplicationId(),
                            payment.getAmount(),
                            collection.getCurrencyCode(),
                            payment.getPaidAt(),
                            payment.getPaymentReference()
                    );
                })
                .toList();
    }
}
