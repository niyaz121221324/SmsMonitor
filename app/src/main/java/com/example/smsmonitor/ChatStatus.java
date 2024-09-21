package com.example.smsmonitor;

import com.google.gson.annotations.SerializedName;

public class ChatStatus {
    private User user;
    private String status;
    @SerializedName("until_date")
    private long untilDate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(long untilDate) {
        this.untilDate = untilDate;
    }
}
