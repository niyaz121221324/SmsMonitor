package com.example.smsmonitor;

import java.util.List;

public class TelegramGetUpdatesResponse {
    private boolean ok;
    private List<Result> result;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }
}
