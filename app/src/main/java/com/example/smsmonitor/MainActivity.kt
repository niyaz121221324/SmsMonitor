package com.example.smsmonitor

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum

class MainActivity : ComponentActivity() {
    private lateinit var hubConnection: HubConnection
    private lateinit var smsReceiver: SmsReceiver
    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmsMonitorApp() // Устанавливаем контент с использованием Compose
        }

        if (!isSmsPermissionGranted()) {

            // Запрашиваем доступ к SMS
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS),
                requestCode
            )
        }
    }

    private fun isSmsPermissionGranted() : Boolean {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS
        )

        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun registerSmsReceiver(userNameText: String, monitoredPhoneNumbers: String) {
        // Подключаемся к хабу для получения сообщений с сервера
        connectHub(userNameText)

        smsReceiver = SmsReceiver(userNameText, monitoredPhoneNumbers, this)

        // Создаём новый IntentFilter для SMS_RECEIVED
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)

        // Зарегистрируйте приемник с фильтром
        registerReceiver(smsReceiver, filter)
    }

    private fun connectHub(userNameText: String) {
        val hubUrl = "https://glider-dear-hog.ngrok-free.app/notificationHub"

        hubConnection = HubConnectionBuilder.create(hubUrl)
            .withTransport(TransportEnum.WEBSOCKETS) // Только WebSocket
            .build()

        hubConnection.start().blockingAwait()

        // Вызовите метод RegisterUserAsync после подключения.
        hubConnection.send("RegisterUserAsync", userNameText)

        hubConnection
            .on("ReceiveMessage", { message:SmsMessage ->
                onMessageReceived(message)
            }, SmsMessage::class.java)
    }

    // Отправляет sms на указанный номер в объекте message
    private fun onMessageReceived(message: SmsMessage) {
        try {
            val smsManager:SmsManager = this.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(message.phoneNumber, null, message.messageContent, null, null)
        } catch (e:Exception) {
            Log.d("Error", "Occurred", e)
        }
    }

    private fun unregisterSmsReceiver() {
        if (::smsReceiver.isInitialized) {
            unregisterReceiver(smsReceiver)
            hubConnection.stop()
        }
    }

    @Composable
    fun SmsMonitorApp() {
        var phoneNumbersText by remember { mutableStateOf("+9989") } // Состояние для хранения текста ввода
        var userNameText by remember { mutableStateOf("") }

        Column {
            TextField(
                value = phoneNumbersText,
                onValueChange = { phoneNumbersText = it },
                label = { Text("Введите номера телефонов, с которых будем прослушивать SMS") },
            )
            TextField(
                value = userNameText,
                onValueChange = { userNameText = it },
                label = { Text("Введите telegram userName на который будут отправлятся SMS") }
            )
            Button( onClick = { onClick(userNameText, phoneNumbersText) } ) {
                Text("Зарегистрировать Receiver")
            }
            Button( onClick = { onClick() } ) {
                Text("Удалить Receiver")
            }
        }
    }

    // Метод для блокировки прослушивания SMS
    private fun onClick() {
        unregisterSmsReceiver()
    }

    // Метод для регистрации SMS прослушивателя
    private fun onClick(userNameText: String, phoneNumbersText: String) {
        if (isSmsPermissionGranted()) {
            registerSmsReceiver(userNameText, phoneNumbersText)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SmsMonitorAppPreview() {
        SmsMonitorApp()
    }
}