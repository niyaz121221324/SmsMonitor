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

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private lateinit var smsReceiver: SmsReceiver
    private lateinit var signalRManager: SignalRManager
    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmsMonitorApp() // Устанавливаем контент с использованием Compose
        }

        authManager = AuthManager()
        signalRManager = SignalRManager()

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
            Button( onClick = { onRegister(userNameText, phoneNumbersText) } ) {
                Text("Зарегистрировать Receiver")
            }
            Button( onClick = { onUnregister() } ) {
                Text("Удалить Receiver")
            }
        }
    }

    // Метод для блокировки прослушивания SMS
    private fun onUnregister() {
        if (::smsReceiver.isInitialized) {
            unregisterReceiver(smsReceiver)
            signalRManager.disconnect()
        }
    }

    // Метод для регистрации SMS прослушивателя
    private fun onRegister(userNameText: String, phoneNumbersText: String) {
        if (!isSmsPermissionGranted()) {
            return
        }

        authenticateUser(userNameText) {
            val accessToken = it.accessToken
            connectToSignalR(accessToken)
            registerSmsReceiver(userNameText, phoneNumbersText)
        }
    }

    private fun authenticateUser(userName: String ,onSuccess: (AuthResponse) -> Unit) {
        authManager.authenticate(userName, object : AuthManager.AuthCallback {
            override fun onSuccess(authResponse: AuthResponse) {
                onSuccess(authResponse)
            }

            override fun onFailure(t: Throwable) {
                println("Authentication failed: ${t.message}")
            }
        })
    }

    private fun connectToSignalR(accessToken: String) {
        signalRManager.connect(accessToken)
    }

    private fun registerSmsReceiver(userNameText: String, phoneNumbersText: String) {
        smsReceiver = SmsReceiver(userNameText, phoneNumbersText)
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, filter)
    }

    @Preview(showBackground = true)
    @Composable
    fun SmsMonitorAppPreview() {
        SmsMonitorApp()
    }
}