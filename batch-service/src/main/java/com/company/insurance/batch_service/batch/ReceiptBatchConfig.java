package com.company.insurance.batch_service.batch;

import com.company.insurance.batch_service.client.*;
import com.company.insurance.batch_service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ReceiptBatchConfig {

    private static final Logger log = LoggerFactory.getLogger(ReceiptBatchConfig.class);

    @Bean
    @StepScope
    public ListItemReader<PaymentSummaryDto> paymentItemReader(
            CollectionServiceClient collectionClient,
            DocumentServiceClient documentClient) {

        List<PaymentSummaryDto> allPayments = collectionClient.getAllPayments();
        List<ProcessedPaymentDto> processedList = documentClient.getProcessedPayments();

        Map<Long, Boolean> processedMap = processedList.stream()
                .collect(Collectors.toMap(ProcessedPaymentDto::collectionPaymentId, ProcessedPaymentDto::emailSent, (e1, e2) -> e1));


        List<PaymentSummaryDto> pending = allPayments.stream()
                .filter(p -> !processedMap.containsKey(p.id()) || Boolean.FALSE.equals(processedMap.get(p.id())))
                .toList();

        log.info("Reader çalıştı. Toplam taranan: {}, İşlenecek: {}", allPayments.size(), pending.size());
        return new ListItemReader<PaymentSummaryDto>(pending);
    }

    @Bean
    public ItemProcessor<PaymentSummaryDto, ReceiptProcessingItem> receiptItemProcessor(
            ApplicationServiceClient applicationClient,
            InsuranceAppClient insuranceAppClient) {

        return payment -> {
            ApplicationDto application = applicationClient.getApplicationById(payment.applicationId());
            CustomerDto customer = insuranceAppClient.getCustomerById(application.customerId());

            if (customer.email() == null || customer.email().isBlank()) {
                log.warn("Müşteri emaili boş! Atlanıyor. Customer ID: {}", customer.customerId());
                return null;
            }

            String customerName = ((customer.name() != null ? customer.name() : "") + " " +
                    (customer.surname() != null ? customer.surname() : "")).trim();

            ReceiptCreateRequest createRequest = new ReceiptCreateRequest(
                    payment.id(), payment.applicationId(), customer.email(),
                    payment.amount(), payment.currencyCode(), false);

            return new ReceiptProcessingItem(payment, customerName, createRequest);
        };
    }

    @Bean
    public ItemWriter<ReceiptProcessingItem> receiptItemWriter(
            DocumentServiceClient documentClient, EmailServiceClient emailClient) {
        return chunk -> {
            for (ReceiptProcessingItem item : chunk) {

                ReceiptResponse saved = documentClient.saveReceipt(item.createRequest());


                String emailBody = buildReceiptHtml(saved.receiptNumber(), item.customerName(), item.payment());
                boolean emailSent = false;
                try {
                    EmailSendResponse response = emailClient.sendEmail(new EmailSendRequest(
                            item.createRequest().customerEmail(),
                            "Ödeme Makbuzunuz: " + saved.receiptNumber(),
                            emailBody));
                    emailSent = response != null && response.success();
                } catch (Exception ex) {
                    log.error("Email gönderim hatası. Payment ID: {}, Hata: {}", item.payment().id(), ex.getMessage());
                }


                documentClient.saveReceipt(new ReceiptCreateRequest(
                        item.createRequest().collectionPaymentId(), item.createRequest().applicationId(),
                        item.createRequest().customerEmail(), item.createRequest().amount(),
                        item.createRequest().currencyCode(), emailSent));
            }
        };
    }

    @Bean
    public Step receiptStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ItemReader<PaymentSummaryDto> paymentItemReader,
                            ItemProcessor<PaymentSummaryDto, ReceiptProcessingItem> receiptItemProcessor,
                            ItemWriter<ReceiptProcessingItem> receiptItemWriter) {
        return new StepBuilder("receiptStep", jobRepository)
                .<PaymentSummaryDto, ReceiptProcessingItem>chunk(10)
                .reader(paymentItemReader)
                .processor(receiptItemProcessor)
                .writer(receiptItemWriter)
                .transactionManager(transactionManager)
                .faultTolerant()
                .skipLimit(50)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job receiptJob(JobRepository jobRepository, Step receiptStep) {
        return new JobBuilder("receiptJob", jobRepository)
                .start(receiptStep)
                .build();
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