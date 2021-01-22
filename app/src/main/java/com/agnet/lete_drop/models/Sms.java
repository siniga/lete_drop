package com.agnet.lete_drop.models;

public class Sms {
    private String from, to, text;

    public Sms(String from, String to, String text){
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public String getText() {
        return text;
    }

    public String getTo() {
        return to;
    }
}
