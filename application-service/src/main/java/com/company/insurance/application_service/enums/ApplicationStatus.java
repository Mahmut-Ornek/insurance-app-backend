package com.company.insurance.application_service.enums;

public enum ApplicationStatus {
    PENDING("P"), APPROVED("A"), REJECTED("R"), CANCELLED("C");

    private final String code;

    ApplicationStatus(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static ApplicationStatus fromCode(String code){
        for(ApplicationStatus status : values()){
            if (status.code.equals(code)) return status;
        }
        throw new IllegalArgumentException("Bilinmeyen statü kodu: " + code);
    }
}
