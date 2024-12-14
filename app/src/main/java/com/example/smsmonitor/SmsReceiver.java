package com.example.smsmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.HashSet;
import java.util.Objects;

public class SmsReceiver extends BroadcastReceiver {

    private final String _userName;
    private final HashSet<String> _monitoredPhoneNumbers;
    private final TelegramManager _telegramManager;

    // Идентификатор чата на который будут отправляться сообщения
    private long _chatId;

    public SmsReceiver(@NonNull String userName, String monitoredPhoneNumbersString) {
        _userName = userName.trim();
        _telegramManager = new TelegramManager();

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
        _telegramManager.getChatId(_userName, new TelegramManager.GetChatIdCallback() {
            @Override
            public void onSuccess(Long chatId) {
                _chatId = chatId;
            }

            @Override
            public void onFailure(Throwable t) {
                throw new RuntimeException(t);
            }
        });
     }

    private void sendMessage(Message smsMessage) {
        if (_chatId == 0) {
            return;
        }

        _telegramManager.sendMessage(_chatId, smsMessage, new TelegramManager.SendMessageCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i("Telegram", "Message sent successfully");
            }

            @Override
            public void onFailure(Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }
}