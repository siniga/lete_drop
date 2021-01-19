package com.agnet.lete_drop.models;

public class RoutePath {

    String phone;
    int time;
    int partnerId, driverId;

    public RoutePath(String phone, int time, int partnerId, int driverId){
        this.phone =phone;
        this.time = time;
        this.partnerId = partnerId;
        this.driverId = driverId;

    }

    public String getPhone() {
        return phone;
    }

    public int getTime() {
        return time;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public int getDriverId() {
        return driverId;
    }
}

