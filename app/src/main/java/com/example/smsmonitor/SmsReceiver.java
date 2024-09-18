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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsReceiver extends BroadcastReceiver {

    private final String _token;
    private final String _userName;
    private final long _chatId;
    private final OkHttpClient _httpClient;
    private final HashSet<String> _monitoredPhoneNumbers;

    public SmsReceiver(String userName, String monitoredPhoneNumbersString, Context context) {
        _token = context.getString(R.string.telegram_bot_token);
        _httpClient = new OkHttpClient();
        _userName = userName.trim();
        _chatId = getChatId();

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
                String messageBody = smsMessage.getMessageBody();
                String originatedAddress = smsMessage.getOriginatingAddress();

                boolean isSmsMonitored = _monitoredPhoneNumbers.contains(originatedAddress);

                String smsLogMessage = "Received SMS from" + (isSmsMonitored ? " monitoredAddress: " : ": ");

                Log.d(TAG, smsLogMessage + originatedAddress + ", Message: " + messageBody);
            }
        }
    }

    // Получаем идентификатор бота для отправки сообщений
    private long getChatId() {
        Request request = new Request.Builder()
                .url(String.format("https://api.telegram.org/bot%s/getUpdates", _token))
                .build();

        try (Response response = _httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                Gson gson = new Gson();
                TelegramGetUpdatesResponse updates = gson.fromJson(responseBody, TelegramGetUpdatesResponse.class);

                if (updates != null) {
                    Result firstResult = getFirstResultByUserName(updates);

                    if (firstResult != null) {
                        return firstResult.getMessage().getChat().getId();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return 0;
    }

    private Result getFirstResultByUserName(TelegramGetUpdatesResponse updates) {
        for (Result result : updates.getResults()) {
            Message message = result.getMessage();

            if (message != null && message.getChat() != null) {
                Chat chat = message.getChat();
                if (chat.getUserName() != null && chat.getUserName().equals(_userName)){
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
        String urlAddress = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                _token,
                _chatId,
                urlEncode(message));

        Request request = new Request.Builder()
                .url(urlAddress)
                .build();
    }

    private String getMessage(@NonNull SmsMessage smsMessage) {
        String message = "";

        if (smsMessage.getOriginatingAddress() != null && smsMessage.getMessageBody() != null) {
            message = String.format("У вас сообщение от телефона : %s /n %s",
                    smsMessage.getOriginatingAddress(),
                    smsMessage.getMessageBody());
        }

        return urlEncode(message);
    }

    private String urlEncode(String text){
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}