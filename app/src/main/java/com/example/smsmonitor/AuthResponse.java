package com.example.smsmonitor;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accesToken) {
        this.accessToken = accesToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
