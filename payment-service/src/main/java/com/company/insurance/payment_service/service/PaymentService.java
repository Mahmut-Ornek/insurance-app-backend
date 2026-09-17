package com.company.insurance.payment_service.service;

import com.company.insurance.payment_service.client.CollectionServiceClient;
import com.company.insurance.payment_service.dto.CollectionSummaryDto;
import com.company.insurance.payment_service.dto.PaymentInitRequest;
import com.company.insurance.payment_service.dto.PaymentInitResponse;
import com.company.insurance.payment_service.entity.Payment;
import com.company.insurance.payment_service.enums.PaymentStatus;
import com.company.insurance.payment_service.gateway.IyzicoGateway;
import com.company.insurance.payment_service.repository.PaymentRepository;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final IyzicoGateway iyzicoGateway;
    private final CollectionServiceClient collectionClient;

    @Value("${iyzico.callback-url}")
    private String callbackUrl;

    public PaymentService(PaymentRepository paymentRepository, IyzicoGateway iyzicoGateway, CollectionServiceClient collectionClient){
        this.paymentRepository = paymentRepository;
        this.iyzicoGateway = iyzicoGateway;
        this.collectionClient = collectionClient;
    }

    @Transactional
    public PaymentInitResponse initializePayment(PaymentInitRequest request){
        CollectionSummaryDto collection;
        try {
            collection = collectionClient.getCollection(request.collectionId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tahsilat kaydı bulunamadı: " + request.collectionId());
        }

        if (collection.isClosed()) {
            throw new IllegalStateException("Bu tahsilat zaten tamamen kapatılmış!");
        }

        BigDecimal remaining = collection.remainingAmount();
        if (request.amount().compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Ödenmek istenen tutar kalan borçtan fazla! Kalan borç: " + remaining);
        }
        String conversationId = UUID.randomUUID().toString();

        CreateCheckoutFormInitializeRequest iyzicoRequest = new CreateCheckoutFormInitializeRequest();
        iyzicoRequest.setLocale(Locale.TR.getValue());
        iyzicoRequest.setConversationId(conversationId);
        iyzicoRequest.setPrice(request.amount());
        iyzicoRequest.setPaidPrice(request.amount());
        iyzicoRequest.setCurrency(request.currencyCode() != null ? request.currencyCode() : Currency.TRY.name());
        iyzicoRequest.setBasketId("BASKET-" + request.collectionId());
        iyzicoRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());
        iyzicoRequest.setCallbackUrl(callbackUrl);


        Buyer buyer = new Buyer();
        buyer.setId("BY-" + request.collectionId());
        buyer.setName("sigortali");
        buyer.setSurname("musteri");
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail("sigortali@example.com");
        buyer.setIdentityNumber("11111111110");
        buyer.setRegistrationAddress("Istanbul Maslak");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        iyzicoRequest.setBuyer(buyer);

        Address address = new Address();
        address.setContactName("Sigortali Musteri");
        address.setCity("Istanbul");
        address.setCountry("Turkey");
        address.setAddress("Istanbul Maslak");
        iyzicoRequest.setShippingAddress(address);
        iyzicoRequest.setBillingAddress(address);

        List<BasketItem> basketItems = new ArrayList<>();
        BasketItem basketItem = new BasketItem();
        basketItem.setId("BI-" + request.collectionId());
        basketItem.setName("Sigorta Prim Odemesi");
        basketItem.setCategory1("Sigorta");
        basketItem.setItemType(BasketItemType.VIRTUAL.name());
        basketItem.setPrice(request.amount());
        basketItems.add(basketItem);
        iyzicoRequest.setBasketItems(basketItems);

        CheckoutFormInitialize checkoutFormInitialize = iyzicoGateway.initializeCheckoutForm(iyzicoRequest);

        if (!"success".equalsIgnoreCase(checkoutFormInitialize.getStatus())){
            throw new RuntimeException("iyzico başlatılamadı: " + checkoutFormInitialize.getErrorMessage());
        }

        Payment payment = new Payment();
        payment.setCollectionId(request.collectionId());
        payment.setAmount(request.amount());
        payment.setCurrencyCode(request.currencyCode() != null ? request.currencyCode() : "TRY");
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setConversationId(conversationId);
        payment.setToken(checkoutFormInitialize.getToken());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return new PaymentInitResponse(checkoutFormInitialize.getToken(),
                checkoutFormInitialize.getPaymentPageUrl(),
                conversationId
        );
    }

    public String handleCallback(String token){
        Payment payment = paymentRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token ile eşleşen ödeme bulunamadı: " + token));

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            log.warn("Bu odeme zaten islenmis durumda. Status: {}, Token: {}", payment.getStatus(), token);
            return "Bu ödeme oturumu daha önce tamamlanmıştır. Mevcut durum: " + payment.getStatus();
        }
        RetrieveCheckoutFormRequest retrieveRequest = new RetrieveCheckoutFormRequest();
        retrieveRequest.setToken(token);

        CheckoutForm checkoutForm = iyzicoGateway.retrieveCheckoutForm(retrieveRequest);

        boolean isSuccess = "success".equalsIgnoreCase(checkoutForm.getStatus())
                && "SUCCESS".equalsIgnoreCase(checkoutForm.getPaymentStatus());

        if (isSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setIyzicoPaymentId(checkoutForm.getPaymentId());
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            try {
                collectionClient.notifyPaymentSuccess(payment.getCollectionId(), payment.getAmount(), checkoutForm.getPaymentId());
            } catch (Exception e) {
                log.error("CRITICAL: Odeme basarili (Iyzico ID: {}) fakat Collection-Service'e bildirilemedi! Hata: {}",
                        checkoutForm.getPaymentId(), e.getMessage(), e);
                return "Ödemeniz bankadan başarıyla alındı ancak tahsilat kaydı güncellenirken gecikme oluştu. Ekibimiz kısa sürede işleyecektir.";
            }
            return "Ödeme başarıyla tamamlandı ve tahsilata işlendi.";
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(checkoutForm.getErrorMessage());
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            return "Ödeme başarısız oldu: " + checkoutForm.getErrorMessage();
        }
    }
}
