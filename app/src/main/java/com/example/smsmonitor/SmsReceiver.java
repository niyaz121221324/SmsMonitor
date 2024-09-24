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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsReceiver extends BroadcastReceiver {

    private final String _token;
    private final String _userName;
    private final OkHttpClient _httpClient;
    private final HashSet<String> _monitoredPhoneNumbers;

    // Идентификатор чата на который
    private long _chatId;

    // Количество сообщений получаемых с запроса getUpdates
    private static final int OFFSET = 5;

    public SmsReceiver(@NonNull String userName, String monitoredPhoneNumbersString, @NonNull Context context) {
        _token = context.getString(R.string.telegram_bot_token);
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
                        sendMessage(smsMessage);
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
                .url(String.format("https://api.telegram.org/bot%s/getUpdates?offset=%s", _token, OFFSET))
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
        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();

            Gson gson = new Gson();
            TelegramGetUpdatesResponse updates = gson.fromJson(responseBody, TelegramGetUpdatesResponse.class);

            if (updates != null) {
                if (updates.getResult() != null) {
                    Result firstResult = getFirstResultByUserName(updates);

                    if (firstResult != null && firstResult.getMessage() != null) {
                        Chat chat = firstResult.getMessage().getChat();

                        if (chat != null) {
                            _chatId = chat.getId();
                        }
                    }
                }
            }
        }
    }

    private Result getFirstResultByUserName(TelegramGetUpdatesResponse updates) {
        for (Result result : updates.getResult()) {
            Message message = result.getMessage();

            if (message != null && message.getChat() != null) {
                Chat chat = message.getChat();
                if (chat.getUsername() != null && chat.getUsername().equals(_userName)){
                    return result;
                }
            }
        }

        return null;
    }

    private void sendMessage(SmsMessage smsMessage) {
        if (_chatId == 0) {
            return;
        }

        String message = getMessage(smsMessage);

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(String.format("https://api.telegram.org/bot%s/sendMessage", _token)))
                .newBuilder()
                .addQueryParameter("chat_id", String.valueOf(_chatId))
                .addQueryParameter("text", message);

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

        _httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException();
                }
            }
        });
    }

    private String getMessage(@NonNull SmsMessage smsMessage) {
        String message = "";

        if (smsMessage.getOriginatingAddress() != null && smsMessage.getMessageBody() != null) {
            message = String.format("%s : От %s",
                    smsMessage.getMessageBody(),
                    smsMessage.getOriginatingAddress());
        }

        return message;
    }
}