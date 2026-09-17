package com.company.insurance.application_service.service;

import com.company.insurance.application_service.client.CollectionServiceClient;
import com.company.insurance.application_service.client.InsuranceAppClient;
import com.company.insurance.application_service.dto.*;
import com.company.insurance.application_service.entity.Application;
import com.company.insurance.application_service.enums.ApplicationStatus;
import com.company.insurance.application_service.repository.ApplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private InsuranceAppClient insuranceAppClient;

    @Mock
    private CollectionServiceClient collectionServiceClient;

    @InjectMocks
    private ApplicationService applicationService;

    @Nested
    @DisplayName("Başvuru Oluşturma (createApplication) Testleri")
    class CreateApplicationTests {

        @Test
        @DisplayName("Müşterinin aynı ürün için açık PENDING başvurusu varsa IllegalStateException fırlatmalıdır")
        void shouldThrowExceptionWhenPendingApplicationAlreadyExists() {
            ApplicationCreateRequest request = new ApplicationCreateRequest(1L, 2L, 5, 2026);
            when(applicationRepository.existsByCustomerIdAndProductIdAndStatusAndIsDeletedFalse(
                    1L, 2L, ApplicationStatus.PENDING)).thenReturn(true);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    applicationService.createApplication(request));

            assertTrue(exception.getMessage().contains("açık bir başvurusu bulunmaktadır"));
            verifyNoInteractions(insuranceAppClient);
            verify(applicationRepository, never()).save(any(Application.class));
        }

        @Test
        @DisplayName("Açık başvuru yoksa fiyat hesaplayıp PENDING durumunda kaydetmelidir")
        void shouldCreateApplicationSuccessfully() {
            ApplicationCreateRequest request = new ApplicationCreateRequest(1L, 2L, 5, 2026);
            when(applicationRepository.existsByCustomerIdAndProductIdAndStatusAndIsDeletedFalse(
                    1L, 2L, ApplicationStatus.PENDING)).thenReturn(false);

            PriceCalculationResponse priceResponse = new PriceCalculationResponse(1L, 2L, new BigDecimal("10000.00"),
                    new BigDecimal("15288.65"), "TRY", java.util.Collections.emptyMap());
            when(insuranceAppClient.calculatePrice(any(PriceCalculationRequest.class)))
                    .thenReturn(priceResponse);

            when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> {
                Application app = inv.getArgument(0);
                app.setApplicationId(100L);
                return app;
            });

            ApplicationResponse response = applicationService.createApplication(request);

            assertNotNull(response);
            assertEquals(100L, response.applicationId());
            assertEquals(ApplicationStatus.PENDING, response.status());
            assertEquals(0, new BigDecimal("15288.65").compareTo(response.calculatedPrice()));
            assertEquals("TRY", response.currencyCode());

            ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
            verify(applicationRepository).save(captor.capture());
            Application saved = captor.getValue();
            assertEquals(ApplicationStatus.PENDING, saved.getStatus());
            assertNotNull(saved.getAppliedAt());
        }
    }

    @Nested
    @DisplayName("Başvuru Onaylama (approveApplication) Testleri")
    class ApproveApplicationTests {

        @Test
        @DisplayName("PENDING olmayan bir başvuru onaylanmak istendiğinde IllegalStateException fırlatmalıdır")
        void shouldThrowExceptionWhenApprovingNonPendingApplication() {
            Long appId = 1L;
            Application nonPending = new Application();
            nonPending.setApplicationId(appId);
            nonPending.setStatus(ApplicationStatus.REJECTED);

            when(applicationRepository.findByApplicationIdAndIsDeletedFalse(appId))
                    .thenReturn(Optional.of(nonPending));

            ApplicationDecisionRequest decisionRequest = new ApplicationDecisionRequest("admin", null);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    applicationService.approveApplication(appId, decisionRequest)
            );

            assertTrue(exception.getMessage().contains("Sadece PENDING durumundaki başvurular onaylanabilir"));
            verifyNoInteractions(collectionServiceClient);
        }

        @Test
        @DisplayName("PENDING başvuru onaylandığında Collection-Service çağrılmalı ve APPROVED olarak kaydedilmelidir")
        void shouldApproveApplicationAndTriggerCollectionService() {
            Long appId = 1L;
            Application pendingApp = new Application();
            pendingApp.setApplicationId(appId);
            pendingApp.setStatus(ApplicationStatus.PENDING);
            pendingApp.setCalculatedPrice(new BigDecimal("15288.65"));
            pendingApp.setCurrencyCode("TRY");

            when(applicationRepository.findByApplicationIdAndIsDeletedFalse(appId))
                    .thenReturn(Optional.of(pendingApp));
            when(applicationRepository.save(any(Application.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ApplicationDecisionRequest decisionRequest = new ApplicationDecisionRequest("admin_user", null);

            ApplicationResponse response = applicationService.approveApplication(appId, decisionRequest);

            assertEquals(ApplicationStatus.APPROVED, response.status());
            assertEquals("admin_user", response.decidedBy());
            assertNotNull(response.decidedAt());


            ArgumentCaptor<CollectionCreateRequest> collectionCaptor = ArgumentCaptor.forClass(CollectionCreateRequest.class);
            verify(collectionServiceClient).createCollection(collectionCaptor.capture());
            CollectionCreateRequest triggeredRequest = collectionCaptor.getValue();

            assertEquals(appId, triggeredRequest.applicationId());
            assertEquals(0, new BigDecimal("15288.65").compareTo(triggeredRequest.totalAmount()));
            assertEquals("TRY", triggeredRequest.currencyCode());
        }
    }

    @Nested
    @DisplayName("Başvuru Reddetme (rejectApplication) Testleri")
    class RejectApplicationTests {

        @Test
        @DisplayName("Başvuru reddedildiğinde Collection-Service kesinlikle çağrılmamalıdır")
        void shouldRejectApplicationWithoutTriggeringCollectionService() {
            Long appId = 1L;
            Application pendingApp = new Application();
            pendingApp.setApplicationId(appId);
            pendingApp.setStatus(ApplicationStatus.PENDING);

            when(applicationRepository.findByApplicationIdAndIsDeletedFalse(appId))
                    .thenReturn(Optional.of(pendingApp));
            when(applicationRepository.save(any(Application.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ApplicationDecisionRequest decisionRequest = new ApplicationDecisionRequest(
                    "risk_manager",
                    "Müşteri aynı ürünü yakın zamanda aldı"
            );

            ApplicationResponse response = applicationService.rejectApplication(appId, decisionRequest);

            assertEquals(ApplicationStatus.REJECTED, response.status());
            assertEquals("risk_manager", response.decidedBy());
            assertEquals("Müşteri aynı ürünü yakın zamanda aldı", response.description());
            assertNotNull(response.decidedAt());

            verifyNoInteractions(collectionServiceClient);
        }
    }
}
