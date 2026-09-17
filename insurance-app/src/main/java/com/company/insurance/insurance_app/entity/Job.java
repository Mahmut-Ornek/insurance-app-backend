package com.company.insurance.insurance_app.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Short risk;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getRisk() {
        return risk;
    }

    public void setRisk(Short risk) {
        this.risk = risk;
    }
}
