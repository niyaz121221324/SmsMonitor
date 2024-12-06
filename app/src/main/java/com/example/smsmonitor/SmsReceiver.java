package com.example.smsmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SmsReceiver extends BroadcastReceiver {

    private final String _userName;
    private final OkHttpClient _httpClient;
    private final HashSet<String> _monitoredPhoneNumbers;

    // Идентификатор чата на который будут отправляться сообщения
    private long _chatId;

    public SmsReceiver(@NonNull String userName, String monitoredPhoneNumbersString) {
        _httpClient = new OkHttpClient();
        _userName = userName.trim();

        // Получаем chat_id для отправки сообщений
        initializeChatId();

        // Инициализировать набор отслеживаемых телефонных номеров
        _monitoredPhoneNumbers = new HashSet<>();
        setMonitoredPhoneNumbers(monitoredPhoneNumbersString);
    }

    private void setMonitoredPhoneNumbers(@NonNull String monitoredPhoneNumbersString) {
        String[] phoneNumbers = monitoredPhoneNumbersString.split(",");

        for (String phoneNumber : phoneNumbers) {
            _monitoredPhoneNumbers.add(phoneNumber.trim());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Objects.equals(intent.getAction(), "android.provider.Telephony.SMS_RECEIVED")) {
            return;
        }

        SmsMessage[] smsArray = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        if (smsArray != null) {
            if (_chatId > 0) {
                for (SmsMessage smsMessage : smsArray) {
                    if (_monitoredPhoneNumbers.contains(smsMessage.getOriginatingAddress())) {
                        sendMessage(Message.from(smsMessage));
                    }
                }
            } else {
                initializeChatId();
            }
        }
    }

    // Получаем идентификатор бота для отправки сообщений
    private void initializeChatId() {
        Request request = new Request.Builder()
                .url(String.format("https://glider-dear-hog.ngrok-free.app/getChatId?userName=%s", _userName))
                .build();

        _httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                setChatIdValue(response);
            }
        });
     }

    private void setChatIdValue(@NonNull Response response) throws IOException {
        if (response.isSuccessful() && response.body() != null && _chatId == 0) {
            String responseBody = response.body().string();

            Gson gson = new Gson();
            _chatId = gson.fromJson(responseBody, long.class);
        }
    }

    private void sendMessage(Message smsMessage) {
        if (_chatId == 0) {
            return;
        }

        String url = String.format("https://glider-dear-hog.ngrok-free.app/sendMessage?chatId=%s", _chatId);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = new Gson().toJson(smsMessage);

        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        _httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                throw new RuntimeException("Request to sendMessage endpoint failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response.code());
                }
            }
        });
    }
}