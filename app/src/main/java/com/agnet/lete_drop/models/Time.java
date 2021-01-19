package com.agnet.lete_drop.models;

public class Time {

    int timeCountDown;
    String time;


    public Time(String time, int timeCountDown){
       this.time = time;
       this.timeCountDown = timeCountDown;

    }

    public String getTime() {
        return time;
    }

    public int getTimeCountDown() {
        return timeCountDown;
    }
}
