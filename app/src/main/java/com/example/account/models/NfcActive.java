package com.example.account.models;

public class NfcActive {
    private String idCard;
    private String createdAt;
    private String imei;

    public NfcActive(String idCard, String createdAt, String imei) {
        this.idCard = idCard;
        this.createdAt  = createdAt ;
        this.imei = imei;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getCreatedAt() {
        return createdAt ;
    }

    public String getImei() {
        return imei;
    }
}
