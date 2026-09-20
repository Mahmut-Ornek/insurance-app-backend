package com.company.insurance.document_service.service;

import com.company.insurance.document_service.client.ApplicationServiceClient;
import com.company.insurance.document_service.client.CollectionServiceClient;
import com.company.insurance.document_service.client.EmailServiceClient;
import com.company.insurance.document_service.client.InsuranceAppClient;
import com.company.insurance.document_service.dto.*;
import com.company.insurance.document_service.entity.Receipt;
import com.company.insurance.document_service.repository.ReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ReceiptService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptService.class);

    private final ReceiptRepository receiptRepository;
    private final CollectionServiceClient collectionServiceClient;
    private final ApplicationServiceClient applicationServiceClient;
    private final InsuranceAppClient insuranceAppClient;
    private final EmailServiceClient emailServiceClient;

    public ReceiptService(ReceiptRepository receiptRepository,
                          CollectionServiceClient collectionServiceClient,
                          ApplicationServiceClient applicationServiceClient,
                          InsuranceAppClient insuranceAppClient,
                          EmailServiceClient emailServiceClient) {
        this.receiptRepository = receiptRepository;
        this.collectionServiceClient = collectionServiceClient;
        this.applicationServiceClient = applicationServiceClient;
        this.insuranceAppClient = insuranceAppClient;
        this.emailServiceClient = emailServiceClient;
    }

    public BatchReceiptResponse generateBatchReceipts() {
        List<PaymentSummaryDto> allPayments = collectionServiceClient.getAllPayments();
        Set<Long> existingPaymentIds = receiptRepository.findAllExistingPaymentIds();

        // Henüz makbuzu oluşturulmamış ödemeleri filtrele
        List<PaymentSummaryDto> pendingPayments = allPayments.stream()
                .filter(p -> !existingPaymentIds.contains(p.id()))
                .toList();

        int successCount = 0;
        List<Long> failedPaymentIds = new ArrayList<>();

        for (PaymentSummaryDto payment : pendingPayments) {
            try {
                processSingleReceipt(payment);
                successCount++;
            } catch (Exception ex) {
                log.error("Makbuz üretimi sırasında hata oluştu. Payment ID: {}, Hata: {}", payment.id(), ex.getMessage());
                failedPaymentIds.add(payment.id());
            }
        }

        String message = String.format("Toplu makbuz işlemi tamamlandı. Toplam taranan: %d, Üretilen: %d, Başarısız: %d",
                pendingPayments.size(), successCount, failedPaymentIds.size());

        return new BatchReceiptResponse(
                pendingPayments.size(),
                successCount,
                failedPaymentIds.size(),
                failedPaymentIds,
                message
        );
    }

    private void processSingleReceipt(PaymentSummaryDto payment) {
        // 1. Başvuru ve Müşteri bilgilerini çek
        ApplicationDto application = applicationServiceClient.getApplicationById(payment.applicationId());
        CustomerDto customer = insuranceAppClient.getCustomerById(application.customerId());

        // 2. Receipt kaydını başlat ve kaydet (ID almak için)
        Receipt receipt = new Receipt();
        receipt.setCollectionPaymentId(payment.id());
        receipt.setApplicationId(payment.applicationId());
        receipt.setCustomerEmail(customer.email());
        receipt.setAmount(payment.amount());
        receipt.setCurrencyCode(payment.currencyCode());
        receipt.setEmailSent(false);
        receipt.setGeneratedAt(LocalDateTime.now());
        receipt.setReceiptNumber("PENDING");

        Receipt savedReceipt = receiptRepository.save(receipt);

        // 3. Garantili benzersiz MK-{id} formatında makbuz numarasını ata
        String receiptNumber = "MK-" + savedReceipt.getId();
        savedReceipt.setReceiptNumber(receiptNumber);

        // 4. Müşteri tam adı ve HTML E-posta içeriği
        String customerName = customer.name() + " " + customer.surname();
        String emailBody = buildReceiptHtml(receiptNumber, customerName, payment);

        // 5. Email-Service üzerinden e-postayı gönder ve success durumunu aktar
        try {
            EmailSendResponse emailResponse = emailServiceClient.sendEmail(new EmailSendRequest(
                    customer.email(),
                    "Ödeme Makbuzunuz: " + receiptNumber,
                    emailBody
            ));
            savedReceipt.setEmailSent(emailResponse != null && emailResponse.success());
        } catch (Exception ex) {
            log.warn("E-posta servisi çağrısında hata oluştu. Receipt ID: {}, Hata: {}",
                    savedReceipt.getId(), ex.getMessage());
            savedReceipt.setEmailSent(false);
        }

        receiptRepository.save(savedReceipt);
    }

    private String buildReceiptHtml(String receiptNumber, String customerName, PaymentSummaryDto payment) {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; padding: 24px;">
                    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px;">Ödeme Makbuzu</h2>
                    <p>Sayın <b>%s</b>,</p>
                    <p>Poliçe ödemeniz başarıyla sistemimize yansımış olup makbuz detaylarınız aşağıda yer almaktadır:</p>
                    <table style="width: 100%%; border-collapse: collapse; margin-top: 16px;">
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><b>Makbuz No:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><b>Ödeme Referansı:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><b>Ödenen Tutar:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s %s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><b>Ödeme Tarihi:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td>
                        </tr>
                    </table>
                    <p style="margin-top: 24px; font-size: 13px; color: #7f8c8d;">Bu e-posta sistem tarafından otomatik olarak üretilmiştir.</p>
                </div>
                """.formatted(
                customerName,
                receiptNumber,
                payment.paymentReference(),
                payment.amount(),
                payment.currencyCode(),
                payment.paidAt()
        );
    }
}