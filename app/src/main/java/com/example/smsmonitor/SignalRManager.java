package com.example.smsmonitor;

import android.telephony.SmsManager;
import android.util.Log;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import io.reactivex.rxjava3.core.Single;

public class SignalRManager {
    private static final String TAG = "SignalRManager";
    private static final String SIGNALR_URL = "https://glider-dear-hog.ngrok-free.app/notificationHub";
    private HubConnection hubConnection;

    /**
     * Устанавливает соединение с SignalR с использованием заданного токена доступа.
     *
     * @param accessToken Токен доступа, используемый для аутентификации.
     */
    public void connect(String accessToken) {
        hubConnection = HubConnectionBuilder.create(SIGNALR_URL)
                .withAccessTokenProvider(Single.defer(() -> Single.just(accessToken)))
                .build();

        try {
            hubConnection.start().blockingAwait();
            Log.i(TAG, "Connected to SignalR hub");

            // Зарегистрируйте прослушиватель событий для получения сообщений
            hubConnection.on("ReceiveMessage", this::sendMessage, Message.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to connect to SignalR hub", e);
        }
    }

    /**
     * Отправляет SMS-сообщение, используя предоставленный объект Message.
     *
     * @param message Объект Message, содержащий номер телефона и содержание сообщения.
     */
    private void sendMessage(Message message) {
        if (message == null) {
            Log.e(TAG, "Received null message");
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    message.getPhoneNumber(),
                    null,
                    message.getMessageContent(),
                    null,
                    null
            );
            Log.i(TAG, "SMS sent to " + message.getPhoneNumber());
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS to " + message.getPhoneNumber(), e);
        }
    }

    public void disconnect() {
        if (hubConnection != null) {
            hubConnection.stop();
        }
    }
}
