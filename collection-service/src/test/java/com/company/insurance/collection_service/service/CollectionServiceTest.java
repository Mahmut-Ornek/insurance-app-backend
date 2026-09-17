package com.company.insurance.collection_service.service;

import com.company.insurance.collection_service.dto.CollectionCreateRequest;
import com.company.insurance.collection_service.dto.CollectionPaymentItemDto;
import com.company.insurance.collection_service.dto.CollectionResponse;
import com.company.insurance.collection_service.entity.Collection;
import com.company.insurance.collection_service.entity.CollectionPayment;
import com.company.insurance.collection_service.repository.CollectionPaymentRepository;
import com.company.insurance.collection_service.repository.CollectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {
    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionPaymentRepository paymentRepository;

    @InjectMocks
    private CollectionService collectionService;

    @Nested
    @DisplayName("Tahsilat oluşturma (createCollection) Testleri")
    class CreateCollectionTests {
        @Test
        @DisplayName("Yeni tahsilat oluşturulurken collectedAmount 0 ve isClosed false olmalıdır")
        void shouldInitializeCollectionWithZeroAmountAndOpenStatus(){
            CollectionCreateRequest request = new CollectionCreateRequest(1L, new BigDecimal("1500.00"),
                    "TRY");

            when(collectionRepository.save(any(Collection.class))).thenAnswer(invocation -> {
                Collection entity = invocation.getArgument(0);
                entity.setId(10L);
                return entity;
            });

            CollectionResponse response = collectionService.create(request);

            assertNotNull(response);
            assertEquals(10L, response.id());
            assertEquals(0, new BigDecimal("0").compareTo(response.collectedAmount()));
            assertFalse(response.isClosed());

            ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
            verify(collectionRepository).save(captor.capture());
            Collection captured = captor.getValue();
            assertEquals(0, new BigDecimal("1500.00").compareTo(captured.getTotalAmount()));
            assertEquals("TRY", captured.getCurrencyCode());
            assertNotNull(captured.getOpenedAt());
        }
    }

    @Nested
    @DisplayName("Ödeme Kaydetme (recordPayment) Testleri")
    class RecordPaymentTests {
        @Test
        @DisplayName("Zaten kapatılan bir tahsilata ödeme yapılmak istendiğinde IllegalStateException fırlatmalıdır.")
        void shouldThrowExceptionWhenCollectionIsAlreadyClosed(){
            Long collectionId = 1L;
            Collection closedCollection = new Collection();
            closedCollection.setId(collectionId);
            closedCollection.setClosed(true);

            when(collectionRepository.findByIdAndIsDeletedFalse(collectionId))
                    .thenReturn(Optional.of(closedCollection));

            CollectionPaymentItemDto paymentDto = new CollectionPaymentItemDto(
                    new BigDecimal("200.00"),
                    LocalDateTime.now(),
                    "REF-001"
            );

            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    collectionService.recordPayment(collectionId, paymentDto)
            );

            assertEquals("Bu tahsilat zaten kapatılmıştır, yeni ödeme yapılamaz!", exception.getMessage());
            verify(paymentRepository, never()).save(any(CollectionPayment.class));
        }

        @Test
        @DisplayName("Aynı paymentReference tekrar geldiğinde yeni ödeme satırı kaydetmemeli ve mevcut hali dönmelidir")
        void shouldNotDuplicatePaymentWhenPaymentReferenceAlreadyExists() {
            Long collectionId = 1L;
            String duplicateRef = "REF-DUPLICATE";

            Collection existingCollection = new Collection();
            existingCollection.setId(collectionId);
            existingCollection.setTotalAmount(new BigDecimal("1000.00"));
            existingCollection.setCollectedAmount(new BigDecimal("500.00"));
            existingCollection.setClosed(false);

            when(collectionRepository.findByIdAndIsDeletedFalse(collectionId))
                    .thenReturn(Optional.of(existingCollection));
            when(paymentRepository.existsByPaymentReference(duplicateRef))
                    .thenReturn(true);

            CollectionPaymentItemDto paymentDto = new CollectionPaymentItemDto(
                    new BigDecimal("500.00"),
                    LocalDateTime.now(),
                    duplicateRef
            );

            CollectionResponse response = collectionService.recordPayment(collectionId, paymentDto);

            assertNotNull(response);
            assertEquals(0, new BigDecimal("500.00").compareTo(response.collectedAmount()));
            verify(paymentRepository, never()).save(any(CollectionPayment.class));
            verify(collectionRepository, never()).save(any(Collection.class));
        }

        @Test
        @DisplayName("Kısmi ödeme yapıldığında toplanan tutar artmalı ama isClosed false kalmalıdır")
        void shouldUpdateCollectedAmountAndKeepOpenOnPartialPayment() {
            Long collectionId = 1L;
            Collection collection = new Collection();
            collection.setId(collectionId);
            collection.setTotalAmount(new BigDecimal("1000.00"));
            collection.setCollectedAmount(new BigDecimal("200.00"));
            collection.setClosed(false);

            when(collectionRepository.findByIdAndIsDeletedFalse(collectionId))
                    .thenReturn(Optional.of(collection));
            when(paymentRepository.existsByPaymentReference("REF-PARTIAL"))
                    .thenReturn(false);
            when(collectionRepository.save(any(Collection.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CollectionPaymentItemDto paymentDto = new CollectionPaymentItemDto(
                    new BigDecimal("300.00"),
                    LocalDateTime.now(),
                    "REF-PARTIAL"
            );

            CollectionResponse response = collectionService.recordPayment(collectionId, paymentDto);

            assertEquals(0, new BigDecimal("500.00").compareTo(response.collectedAmount()));
            assertFalse(response.isClosed());
            assertNull(collection.getClosedAt());
            verify(paymentRepository).save(any(CollectionPayment.class));
        }

        @Test
        @DisplayName("Kalan borcun tamamı ödendiğinde isClosed true olmalı ve closedAt set edilmelidir")
        void shouldCloseCollectionWhenTotalAmountIsFullyCollected() {
            Long collectionId = 1L;
            Collection collection = new Collection();
            collection.setId(collectionId);
            collection.setTotalAmount(new BigDecimal("1000.00"));
            collection.setCollectedAmount(new BigDecimal("600.00"));
            collection.setClosed(false);

            when(collectionRepository.findByIdAndIsDeletedFalse(collectionId))
                    .thenReturn(Optional.of(collection));
            when(paymentRepository.existsByPaymentReference("REF-FINAL"))
                    .thenReturn(false);
            when(collectionRepository.save(any(Collection.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CollectionPaymentItemDto paymentDto = new CollectionPaymentItemDto(
                    new BigDecimal("400.00"),
                    LocalDateTime.now(),
                    "REF-FINAL"
            );

            CollectionResponse response = collectionService.recordPayment(collectionId, paymentDto);

            assertEquals(0, new BigDecimal("1000.00").compareTo(response.collectedAmount()));
            assertTrue(response.isClosed());
            assertNotNull(collection.getClosedAt());
            verify(paymentRepository).save(any(CollectionPayment.class));
        }
    }
}
