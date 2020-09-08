package com.example.account.models;


import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private boolean error;
    private String userId;

    public LoginResponse(boolean error, String userId) {
        this.error = error;
        this.userId = userId;
    }

    public boolean isError() {
        return error;
    }

    public String getUserId() {
        return userId;
    }
}
