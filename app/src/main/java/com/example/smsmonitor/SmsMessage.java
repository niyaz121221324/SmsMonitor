package com.example.smsmonitor;

public class SmsMessage {

    /**
     * Номер телефона, на который будет отправлено сообщение.
     */
    private String phoneNumber = "";

    /**
     * Содержание сообщения.
     */
    private String messageContent = "";

    public SmsMessage() {
    }

    public SmsMessage(String phoneNumber, String messageContent) {
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
