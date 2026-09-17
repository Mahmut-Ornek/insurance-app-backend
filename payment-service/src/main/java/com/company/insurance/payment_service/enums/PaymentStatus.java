package com.company.insurance.payment_service.enums;

public enum PaymentStatus {
    INITIATED("I"),
    SUCCESS("S"),
    FAILED("F");

    private final String code;

    PaymentStatus(String code){
        this.code = code;
    }

    public String getCode(){return code;}

    public static PaymentStatus fromCode(String code){
        if(code == null)return null;
        for(PaymentStatus s : values()){
            if(s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("Bilinmeyen payment status: " + code);
    }
}
