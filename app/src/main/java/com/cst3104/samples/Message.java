package com.cst3104.samples;


import androidx.annotation.NonNull;

public class Message{
    private final String messageTyped;
    private final String timeSent;
    private long id;

    public Message(String messageTyped, String timeSent, long _id) {
        this.messageTyped = messageTyped;
        this.timeSent = timeSent;
        this.id = _id;
    }

    public String getMessageTyped() {
        return messageTyped;
    }

    public String getTimeSent() {
        return timeSent;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    @NonNull
    public String toString() {
        return timeSent  + " :  " + messageTyped ;
    }
}