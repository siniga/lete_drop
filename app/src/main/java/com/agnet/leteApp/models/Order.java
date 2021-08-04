package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Order {

    private int id,status, userId, projectId,outletId;
    private String  deviceTime, createdDate;
    private String lat,lng;

    @SerializedName("order_no")
    private int orderNo;


    public Order(int id, String deviceTime, int orderNo, int status, String lat,
                 String lng, String createdDate, int userId, int projectId, int outletId){
        this.id = id;
        this.deviceTime = deviceTime;
        this.orderNo = orderNo;
        this.status = status;
        this.userId = userId;
        this.createdDate = createdDate;
        this.lat = lat;
        this.lng = lng;
        this.projectId = projectId;
        this.outletId = outletId;
    }


    public int getId() {
        return id;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public int getUserId() {
        return userId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public int getStatus() {
        return status;
    }

    public String getDeviceTime() {
        return deviceTime;
    }


    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public int getOutletId() {
        return outletId;
    }

    public int getProjectId() {
        return projectId;
    }
}

