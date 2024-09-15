package com.example.smsmonitor;

public class From {
    private long _id;
    private boolean _isBot;
    private String _firstName;
    private String _userName;
    private String _languageCode;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public boolean getIsBot() {
        return _isBot;
    }

    public void setIsBot(boolean isBot) {
        _isBot = isBot;
    }

    public String getFirstName() {
        return _firstName;
    }

    public void setFirstName(String firstName) {
        _firstName = firstName;
    }

    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }

    public String getLanguageCode() {
        return _languageCode;
    }

    public void setLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }
}
