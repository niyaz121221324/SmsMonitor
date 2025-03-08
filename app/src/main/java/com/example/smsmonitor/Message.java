package com.example.smsmonitor;

import android.telephony.SmsMessage;

public class Message {

    /**
     * Идентификатор чата, куда будет отправлено сообщение.
     */
    private long chatId;

    /**
     * Номер телефона, на который будет отправлено сообщение.
     */
    private String phoneNumber = "";

    /**
     * Содержание сообщения.
     */
    private String messageContent = "";

    public Message() {
    }

    public Message(long chatId, String phoneNumber, String messageContent) {
        this(phoneNumber, messageContent);
        this.chatId = chatId;
    }

    public Message(String phoneNumber, String messageContent) {
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

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public static Message from(SmsMessage smsMessage) {
        if (smsMessage == null) {
            throw new IllegalArgumentException("SmsMessage cannot be null");
        }

        String phoneNumber = smsMessage.getOriginatingAddress();
        String messageContent = smsMessage.getMessageBody();

        return new Message(phoneNumber, messageContent);
    }
}
