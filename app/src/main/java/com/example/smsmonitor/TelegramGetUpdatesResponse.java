package com.example.smsmonitor;

import java.util.List;

public class TelegramGetUpdatesResponse {
    private boolean _ok;
    private List<Result> _results;

    public boolean getOk() {
        return _ok;
    }

    public void setOk(boolean ok) {
        _ok = ok;
    }

    public List<Result> getResults() {
        return  _results;
    }

    public void setResults (List<Result> results) {
        _results = results;
    }
}
