package com.example.account.models;

public class RdPxList{
    private String datetime;
    private String price;
    public RdPxList(String datetime, String price) {
        this.datetime = datetime;
        this.price = price;
    }



    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getPrice() {
        return price;
    }
}
