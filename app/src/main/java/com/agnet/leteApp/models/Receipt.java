package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Receipt {
    private int id, orderId;
    private String status;

    @SerializedName("rctvnum")
    private String verificationUrl;

    @SerializedName("rctvcode")
    private String verificationCode;

    @SerializedName("znumber")
    private String zNumber;

    @SerializedName("vfdinvoicenum")
    private String vfdInvoiceNum;

    @SerializedName("idate")
    private String date;

    @SerializedName("itime")
    private String time;

    @SerializedName("qrpath")
    private String qrCodePath;

    @SerializedName("qrcode_uri")
    private String qrCodeImg;


    public Receipt(int id, String status, String verificationUrl, String verificationCode,
                   String zNumber, String vfdInvoiceNum, String date, String time, String qrCodePath,
                   String qrCodeImg, int orderId
    ) {
        this.id = id;
        this.status = status;
        this.verificationUrl = verificationUrl;
        this.verificationCode = verificationCode;
        this.zNumber = zNumber;
        this.vfdInvoiceNum = vfdInvoiceNum;
        this.date = date;
        this.time = time;
        this.qrCodePath = qrCodePath;
        this.qrCodeImg = qrCodeImg;
        this.orderId = orderId;

    }


    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public String getQrCodeImg() {
        return qrCodeImg;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public String getVfdInvoiceNum() {
        return vfdInvoiceNum;
    }

    public String getzNumber() {
        return zNumber;
    }

}



