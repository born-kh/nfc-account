package com.example.account.models;

public class ChangePassword {
    private String newPassword;
    private String password;
    private String userId;

    public ChangePassword(String newPassword, String password, String userId) {
        this.newPassword = newPassword;
        this.password = password;
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }
}
