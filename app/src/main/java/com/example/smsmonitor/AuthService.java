package com.example.smsmonitor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth")
    Call<AuthResponse> authenticate(@Body String telegramUserName);

    @POST("/auth/refreshToken")
    Call<AuthResponse> refreshToken(@Body String refreshToken);
}
