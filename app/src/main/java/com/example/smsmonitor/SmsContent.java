package com.example.smsmonitor;

public class SmsContent {
    private final String _messageBody;
    private final String _originatedAddress;

    public SmsContent(String messageBody, String originatedAddress) {
        _messageBody = messageBody;
        _originatedAddress = originatedAddress;
    }

    public String getMessageBody() {
        return _messageBody;
    }

    public String getOriginatedAddress() {
        return _originatedAddress;
    }
}
