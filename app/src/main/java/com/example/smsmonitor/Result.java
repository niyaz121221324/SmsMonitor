package com.example.smsmonitor;

public class Result {
    private long _updateId;
    private Message _message;

    public long getUpdateId() {
        return  _updateId;
    }

    public void setUpdateId(long updateId) {
        _updateId = updateId;
    }

    public Message getMessage() {
        return _message;
    }

    public void setMessage(Message message) {
        _message = message;
    }
}
