package com.example.smsmonitor;

import static android.content.ContentValues.TAG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.HashSet;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class SmsReceiver extends BroadcastReceiver {

    private  final OkHttpClient _httpClient = new OkHttpClient();

    // Список номеров телефона с которых прослушиваем получаем SMS
    private final HashSet<String> _monitoredPhoneNumbers = new HashSet<>();

    public SmsReceiver(String monitoredPhoneNumbersString) {
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
        return "";
    }
}