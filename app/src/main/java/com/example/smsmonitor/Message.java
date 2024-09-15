package com.example.smsmonitor;

public class Message {
    private long _messageId;
    private From _from;
    private Chat _chat;
    private long _date;
    private String _text;

    public long getMessageId() {
        return _messageId;
    }

    public void setMessageId(long messageId) {
        _messageId = messageId;
    }

    public From getFrom() {
        return _from;
    }

    public void setFrom(From from) {
        _from = from;
    }

    public Chat getChat() {
        return _chat;
    }

    public void setChat(Chat chat) {
        _chat = chat;
    }

    public long getDate() {
        return _date;
    }

    public void setDate(long date) {
        _date = date;
    }

    public String getText() {
        return _text;
    }

    public void setText(String text) {
        _text = text;
    }
}
