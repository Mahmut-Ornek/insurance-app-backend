package com.company.insurance.batch_service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ReceiptJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReceiptJobScheduler.class);

    private final JobOperator jobOperator;
    private final Job receiptJob;

    public ReceiptJobScheduler(JobOperator jobOperator, Job receiptJob) {
        this.jobOperator = jobOperator;
        this.receiptJob = receiptJob;
    }

    // Her 60 saniyede bir çalışır
    @Scheduled(fixedDelay = 60000)
    public void runReceiptJob() {
        try {
            log.info("Zamanlanmış Batch Job tetikleniyor...");

            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobOperator.start(receiptJob, params);
        } catch (Exception e) {
            log.error("Batch Job yürütülürken hata oluştu: {}", e.getMessage());
        }
    }
}