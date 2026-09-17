package com.company.insurance.payment_service.service;

import com.company.insurance.payment_service.client.CollectionServiceClient;
import com.company.insurance.payment_service.dto.CollectionSummaryDto;
import com.company.insurance.payment_service.dto.PaymentInitRequest;
import com.company.insurance.payment_service.dto.PaymentInitResponse;
import com.company.insurance.payment_service.entity.Payment;
import com.company.insurance.payment_service.enums.PaymentStatus;
import com.company.insurance.payment_service.gateway.IyzicoGateway;
import com.company.insurance.payment_service.repository.PaymentRepository;
import com.iyzipay.model.CheckoutForm;
import com.iyzipay.model.CheckoutFormInitialize;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private IyzicoGateway iyzicoGateway;

    @Mock
    private CollectionServiceClient collectionClient;

    @InjectMocks
    private PaymentService paymentService;

    @Nested
    @DisplayName("Ödeme Başlatma (initializePayment) Testleri")
    class InitializePaymentTests {

        @Test
        @DisplayName("iyzico başarısız yanıt verirse RuntimeException fırlatmalıdır")
        void shouldThrowExceptionWhenIyzicoInitFails() {
            PaymentInitRequest request = new PaymentInitRequest(1L, new BigDecimal("100.00"), "TRY");

            // 1. Collection mock: açık ve geçerli bir tahsilat dönmeli
            CollectionSummaryDto mockCollection = new CollectionSummaryDto(
                    1L,
                    new BigDecimal("100.00"),
                    BigDecimal.ZERO,
                    false,
                    false
            );
            when(collectionClient.getCollection(1L)).thenReturn(mockCollection);

            // 2. iyzico failure mock
            CheckoutFormInitialize mockInit = new CheckoutFormInitialize();
            mockInit.setStatus("failure");
            mockInit.setErrorMessage("Geçersiz API Anahtarı");

            when(iyzicoGateway.initializeCheckoutForm(any(CreateCheckoutFormInitializeRequest.class)))
                    .thenReturn(mockInit);

            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    paymentService.initializePayment(request)
            );

            // Servis sınıfında fırlattığın hata mesajı kontrolü
            assertNotNull(ex.getMessage());
            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        @DisplayName("iyzico başarılı olursa INITIATED kaydı açmalı ve token dönmelidir")
        void shouldSaveInitiatedPaymentAndReturnUrl() {
            PaymentInitRequest request = new PaymentInitRequest(1L, new BigDecimal("250.00"), "TRY");

            // 1. Collection mock: açık ve geçerli bir tahsilat dönmeli
            CollectionSummaryDto mockCollection = new CollectionSummaryDto(
                    1L,
                    new BigDecimal("250.00"),
                    BigDecimal.ZERO,
                    false,
                    false
            );
            when(collectionClient.getCollection(1L)).thenReturn(mockCollection);

            // 2. iyzico success mock
            CheckoutFormInitialize mockInit = new CheckoutFormInitialize();
            mockInit.setStatus("success");
            mockInit.setToken("mock-token-123");
            mockInit.setPaymentPageUrl("https://sandbox-payment.iyzico.com/123");

            when(iyzicoGateway.initializeCheckoutForm(any(CreateCheckoutFormInitializeRequest.class)))
                    .thenReturn(mockInit);

            PaymentInitResponse response = paymentService.initializePayment(request);

            assertNotNull(response);
            assertEquals("mock-token-123", response.token());
            assertEquals("https://sandbox-payment.iyzico.com/123", response.paymentPageUrl());

            ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentRepository).save(captor.capture());
            Payment saved = captor.getValue();
            assertEquals(PaymentStatus.INITIATED, saved.getStatus());
            assertEquals("mock-token-123", saved.getToken());
            assertEquals(0, new BigDecimal("250.00").compareTo(saved.getAmount()));
        }

        @Test
        @DisplayName("Collection-Service tahsilat kaydını bulamazsa IllegalArgumentException fırlatmalı ve iyzico çağrılmamalıdır")
        void shouldThrowExceptionWhenCollectionNotFound() {
            // Arrange
            PaymentInitRequest request = new PaymentInitRequest(999L, new BigDecimal("100.00"), "TRY");

            when(collectionClient.getCollection(999L))
                    .thenThrow(new RuntimeException("Tahsilat bulunamadı"));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                    paymentService.initializePayment(request)
            );

            verifyNoInteractions(iyzicoGateway);
            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        @DisplayName("Tahsilat dosyası zaten kapalıysa IllegalStateException fırlatmalı ve iyzico çağrılmamalıdır")
        void shouldThrowExceptionWhenCollectionIsAlreadyClosed() {
            // Arrange
            PaymentInitRequest request = new PaymentInitRequest(1L, new BigDecimal("50.00"), "TRY");

            CollectionSummaryDto closedCollection = new CollectionSummaryDto(
                    1L,
                    new BigDecimal("100.00"),
                    new BigDecimal("100.00"),
                    true, // isClosed = true
                    false
            );
            when(collectionClient.getCollection(1L)).thenReturn(closedCollection);

            // Act & Assert
            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    paymentService.initializePayment(request)
            );

            assertTrue(ex.getMessage().contains("kapatılmış"));
            verifyNoInteractions(iyzicoGateway);
            verify(paymentRepository, never()).save(any(Payment.class));
        }

        @Test
        @DisplayName("Ödeme tutarı kalan borçtan büyükse IllegalArgumentException fırlatmalı ve iyzico çağrılmamalıdır")
        void shouldThrowExceptionWhenAmountExceedsRemainingDebt() {
            // Arrange: Toplam 100, ödenen 60 -> Kalan 40. İstenen ödeme: 50
            PaymentInitRequest request = new PaymentInitRequest(1L, new BigDecimal("50.00"), "TRY");

            CollectionSummaryDto partialCollection = new CollectionSummaryDto(
                    1L,
                    new BigDecimal("100.00"),
                    new BigDecimal("60.00"),
                    false,
                    false
            );
            when(collectionClient.getCollection(1L)).thenReturn(partialCollection);

            // Act & Assert
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    paymentService.initializePayment(request)
            );

            assertTrue(ex.getMessage().contains("Ödenmek istenen tutar kalan borçtan fazla"));
            verifyNoInteractions(iyzicoGateway);
            verify(paymentRepository, never()).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("Callback İşleme (handleCallback) Testleri")
    class HandleCallbackTests {

        @Test
        @DisplayName("Daha önce tamamlanmış bir ödeme tekrar çağrılırsa mükerrer işlem yapmamalıdır")
        void shouldReturnEarlyWhenPaymentAlreadyProcessed() {
            Payment existingPayment = new Payment();
            existingPayment.setStatus(PaymentStatus.SUCCESS);

            when(paymentRepository.findByToken("token-already-done"))
                    .thenReturn(Optional.of(existingPayment));

            String result = paymentService.handleCallback("token-already-done");

            assertTrue(result.contains("daha önce tamamlanmıştır"));
            verifyNoInteractions(iyzicoGateway);
            verifyNoInteractions(collectionClient);
        }

        @Test
        @DisplayName("Ödeme başarılı olduğunda durum SUCCESS olmalı ve Collection-Service tetiklenmelidir")
        void shouldCompletePaymentAndNotifyCollectionService() {
            Payment payment = new Payment();
            payment.setCollectionId(10L);
            payment.setAmount(new BigDecimal("500.00"));
            payment.setStatus(PaymentStatus.INITIATED);

            when(paymentRepository.findByToken("token-success"))
                    .thenReturn(Optional.of(payment));

            CheckoutForm mockForm = new CheckoutForm();
            mockForm.setStatus("success");
            mockForm.setPaymentStatus("SUCCESS");
            mockForm.setPaymentId("IYZICO-PAY-999");

            when(iyzicoGateway.retrieveCheckoutForm(any(RetrieveCheckoutFormRequest.class)))
                    .thenReturn(mockForm);

            String result = paymentService.handleCallback("token-success");

            assertTrue(result.contains("başarıyla tamamlandı"));
            assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
            assertEquals("IYZICO-PAY-999", payment.getIyzicoPaymentId());
            assertNotNull(payment.getCompletedAt());

            verify(paymentRepository).save(payment);
            verify(collectionClient).notifyPaymentSuccess(10L, new BigDecimal("500.00"), "IYZICO-PAY-999");
        }

        @Test
        @DisplayName("Collection-Service çökse bile ödeme SUCCESS kalmalı ve işlem rollback olmamalıdır")
        void shouldKeepSuccessEvenIfCollectionServiceFails() {
            Payment payment = new Payment();
            payment.setCollectionId(10L);
            payment.setAmount(new BigDecimal("500.00"));
            payment.setStatus(PaymentStatus.INITIATED);

            when(paymentRepository.findByToken("token-fail-network"))
                    .thenReturn(Optional.of(payment));

            CheckoutForm mockForm = new CheckoutForm();
            mockForm.setStatus("success");
            mockForm.setPaymentStatus("SUCCESS");
            mockForm.setPaymentId("IYZICO-PAY-555");

            when(iyzicoGateway.retrieveCheckoutForm(any(RetrieveCheckoutFormRequest.class)))
                    .thenReturn(mockForm);

            doThrow(new RuntimeException("Collection-Service Connection Timed Out"))
                    .when(collectionClient).notifyPaymentSuccess(anyLong(), any(BigDecimal.class), anyString());

            String result = paymentService.handleCallback("token-fail-network");

            // Servis hata fırlatsa da ödeme başarıyla DB'ye yazılmış olmalı:
            assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
            verify(paymentRepository).save(payment);
            assertTrue(result.contains("tahsilat kaydı güncellenirken gecikme oluştu"));
        }
    }
}