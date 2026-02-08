package com.example.learnhub;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
    private String user, message;
    private long messagetime;

    public ChatMessage() {
    }

    public ChatMessage(String user, String message) {
        this.user = user;
        this.message = message;
        this.messagetime = System.currentTimeMillis();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessagetime() {
        return  messagetime;
    }
    public String format(){
        Date date = new Date(messagetime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }
}
