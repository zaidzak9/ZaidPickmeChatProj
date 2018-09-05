package com.zaid.zaidpickmeproj.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatBody {
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("sender")
    @Expose
    private String sender;

    public ChatBody(String body, String sender) {
        this.body = body;
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

