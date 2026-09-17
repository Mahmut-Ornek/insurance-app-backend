package com.company.insurance.insurance_app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "health_info_disease")
public class HealthDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "health_info_id", nullable = false)
    private Long healthInfoId;

    @Column(name = "disease_id", nullable = false)
    private Long diseaseId;

    @Column(name = "diagnosis_date", nullable = false)
    private LocalDate diagnosisDate;

    @Column(name = "details", length = 500)
    private String details;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHealthInfoId() {
        return healthInfoId;
    }

    public void setHealthInfoId(Long healthInfoId) {
        this.healthInfoId = healthInfoId;
    }

    public Long getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Long diseaseId) {
        this.diseaseId = diseaseId;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}