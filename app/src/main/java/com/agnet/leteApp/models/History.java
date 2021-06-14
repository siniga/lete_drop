package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class History {

    int id, status;

    @SerializedName("order_no")
    String orderNo;

    @SerializedName("created_date")
    String  createdAt;

    @SerializedName("device_order_time")
    String  orderTime;



    public History(int id, String orderNo, String createdAt, String orderTime, int status) {
        this.id = id;
        this.orderNo = orderNo;
        this.createdAt = createdAt;
        this.orderTime = orderTime;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public int getStatus() {
        return status;
    }
}
