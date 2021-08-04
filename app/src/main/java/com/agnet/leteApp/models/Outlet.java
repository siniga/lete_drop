package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Outlet {

    private int id;
    private String name,phone;
    private  Double lat, lng;
    private String location;

    @SerializedName("qr_code")
    private  String qrCode;

    public  Outlet(int id, String name,String phone, String location, String qrCode){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.qrCode = qrCode;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    public String getQrCode() {
        return qrCode;
    }
}
