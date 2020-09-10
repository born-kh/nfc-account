package com.example.account.models;

public class PostContentData {
   private String userId ;
   private String mjId;
   private  double price;
   private String description;

    public PostContentData(String userId, String mjId, double price, String description) {
        this.userId = userId;
        this.mjId = mjId;
        this.price = price;
        this.description = description;
    }
}
