package com.example.smsmonitor;

public class Chat {
    private long _id;
    private String _firstName;
    private String _userName;
    private String _type;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
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

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }
}
