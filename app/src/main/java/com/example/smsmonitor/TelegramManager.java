package com.example.smsmonitor;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelegramManager {
    private final TelegramService telegramService;

    public TelegramManager() {
        telegramService = ApiClient.getClient().create(TelegramService.class);
    }

    public void sendMessage(Long chatId, Message message, SendMessageCallback callback) {
        telegramService.sendMessage(chatId, message).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Sending message failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getChatId(String telegramUserName, GetChatIdCallback callback) {
        telegramService.getChatId(telegramUserName).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Cannot get chatId for this user: " + telegramUserName + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface GetChatIdCallback {
        void onSuccess(Long chatId);

        void onFailure(Throwable t);
    }

    public interface SendMessageCallback {
        void onSuccess(String response);

        void onFailure(Throwable t);
    }
}
