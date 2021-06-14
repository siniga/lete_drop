package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Customer {

    private   int id;
    String  name, district, lat, lon, url, phone, qrCode, vfd_cust_id,vfd_cust_type;

    @SerializedName("customer_type")
    private String type;

    private int serverId;

    @SerializedName("street_id")
    private int streetId;


    public Customer(int id, String name, int streetId, String district, String url, String lat, String lon, int serverId, String phone, String qrCode, String type, String vfd_cust_id, String vfd_cust_type){
        this.id = id;
        this.name = name;
        this.streetId = streetId;
        this.district = district;
        this.url = url;
        this.lat = lat;
        this.lon = lon;
        this.serverId = serverId;
        this.phone = phone;
        this.qrCode = qrCode;
        this.type = type;
        this.vfd_cust_id = vfd_cust_id;
        this.vfd_cust_type = vfd_cust_type;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStreetId() {
        return streetId;
    }

    public String getUrl() {
        return url;
    }

    public String getDistrict() {
        return district;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getPhone() {
        return phone;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getType() {
        return type;
    }

    public int getServerId() {
        return serverId;
    }

    public String getVfdCustId() {
        return vfd_cust_id;
    }

    public String getVfdCustType() {
        return vfd_cust_type;
    }
}
