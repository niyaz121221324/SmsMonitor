package com.example.smsmonitor;

import android.content.Entity;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Message {
    @SerializedName("message_id")
    private long messageId;
    private User from;
    private Chat chat;
    private long date;
    private String text;
    private List<Entity> entities;

    // Getters and Setters
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
