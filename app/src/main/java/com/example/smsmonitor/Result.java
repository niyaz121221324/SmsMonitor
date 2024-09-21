package com.example.smsmonitor;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("update_id")
    private long updateId;
    private Message message;
    @SerializedName("my_chat_member")
    private ChatMember myChatMember;

    // Getters and Setters
    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ChatMember getMyChatMember() {
        return myChatMember;
    }

    public void setMyChatMember(ChatMember myChatMember) {
        this.myChatMember = myChatMember;
    }
}
