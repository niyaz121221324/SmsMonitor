package com.example.smsmonitor;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private final AuthService authService;

    public AuthManager() {
        authService = ApiClient.getClient().create(AuthService.class);
    }

    public void authenticate(String telegramUserName, AuthCallback callback) {
        authenticateOrRefreshToken(telegramUserName, callback, false);
    }

    public void refreshToken(String refreshToken, AuthCallback callback) {
        authenticateOrRefreshToken(refreshToken, callback, true);
    }

    private void authenticateOrRefreshToken(String token, AuthCallback callback, boolean isRefreshToken) {
        Call<AuthResponse> call = isRefreshToken ? authService.refreshToken(token) : authService.authenticate(token);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = isRefreshToken
                            ? "Failed to refresh token: " + response.code()
                            : "Authentication failed: " + response.code();
                    callback.onFailure(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(AuthResponse authResponse);

        void onFailure(Throwable t);
    }
}
