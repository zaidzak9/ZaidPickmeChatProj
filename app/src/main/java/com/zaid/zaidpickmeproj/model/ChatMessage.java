package com.zaid.zaidpickmeproj.model;

/*
    This file can be edited as per needs,more items can be added to enhance the chat
    functionality.
 */

public class ChatMessage {
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private String Histroydatetime;

    public String getHistroydatetime() {
        return Histroydatetime;
    }

    public void setHistroydatetime(String histroydatetime) {
        Histroydatetime = histroydatetime;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsme() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }
}
