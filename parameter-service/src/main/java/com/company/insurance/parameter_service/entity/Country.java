package com.company.insurance.parameter_service.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country {

    @Id
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String definition;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

}
