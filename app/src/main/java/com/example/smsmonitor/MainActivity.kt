package com.example.smsmonitor

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
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

class MainActivity : ComponentActivity() {
    private lateinit var smsReceiver: SmsReceiver
    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmsMonitorApp() // Устанавливаем контент с использованием Compose
        }

        if (!isReceiveSmsPermissionGranted()) {

            // Запрашиваем доступ к SMS
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS),
                requestCode
            )
        }
    }

    private fun isReceiveSmsPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun registerSmsReceiver(monitoredPhoneNumbers: String) {
        smsReceiver = SmsReceiver(monitoredPhoneNumbers)

        // Создаём новый IntentFilter для SMS_RECEIVED
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)

        // Зарегистрируйте приемник с фильтром
        registerReceiver(smsReceiver, filter)
    }

    private fun unregisterSmsReceiver() {
        if (::smsReceiver.isInitialized) {
            unregisterReceiver(smsReceiver)
        }
    }

    @Composable
    fun SmsMonitorApp() {
        var text by remember { mutableStateOf("+9989") } // Состояние для хранения текста ввода

        Column {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Введите номера телефонов, с которых будем прослушивать SMS") },
            )
            Button( onClick = { onClick(text) } ) {
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
    private fun onClick(text: String) {
        if (isReceiveSmsPermissionGranted()) {
            registerSmsReceiver(text)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SmsMonitorAppPreview() {
        SmsMonitorApp()
    }
}