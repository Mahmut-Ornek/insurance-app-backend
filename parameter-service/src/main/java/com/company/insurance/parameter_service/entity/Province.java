package com.company.insurance.parameter_service.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "province")
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long provinceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String code;

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
