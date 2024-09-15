package com.example.smsmonitor;

import static android.content.ContentValues.TAG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsReceiver extends BroadcastReceiver {

    private final String _token;
    private final OkHttpClient _httpClient;
    private final HashSet<String> _monitoredPhoneNumbers;

    public SmsReceiver(String monitoredPhoneNumbersString, Context context) {
        _token = context.getString(R.string.telegram_bot_token);
        _httpClient = new OkHttpClient();

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
            for (SmsMessage smsMessage : smsArray) {
                String originatingAddress = smsMessage.getDisplayOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();

                boolean isSmsMonitored = _monitoredPhoneNumbers.contains(originatingAddress);

                String smsLogMessage = "Received SMS from" + (isSmsMonitored ? " monitoredAddress: " : ": ");

                Log.d(TAG, smsLogMessage + originatingAddress + ", Message: " + messageBody);
            }
        }
    }

    // Получаем идентификатор бота для отправки сообщений
    private String getChatId() {
        Request request = new Request.Builder()
                .url(String.format("https://api.telegram.org/%s/getUpdates", _token))
                .addHeader("accept", "application/json")
                .build();

        try (Response response = _httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                TelegramGetUpdatesResponse updates = gson.fromJson(response.toString(), TelegramGetUpdatesResponse.class);

                return updates.getResults().get(0).getMessage().getChat().toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";
    }
}