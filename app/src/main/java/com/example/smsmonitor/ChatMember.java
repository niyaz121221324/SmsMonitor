package com.example.smsmonitor;

import com.google.gson.annotations.SerializedName;

public class ChatMember {
    private Chat chat;
    private User from;
    private long date;
    @SerializedName("old_chat_member")
    private ChatStatus oldChatMember;
    @SerializedName("new_chat_member")
    private ChatStatus newChatMember;

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ChatStatus getOldChatMember() {
        return oldChatMember;
    }

    public void setOldChatMember(ChatStatus oldChatMember) {
        this.oldChatMember = oldChatMember;
    }

    public ChatStatus getNewChatMember() {
        return newChatMember;
    }

    public void setNewChatMember(ChatStatus newChatMember) {
        this.newChatMember = newChatMember;
    }
}
