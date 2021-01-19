package com.agnet.lete_drop.models;

import com.google.gson.annotations.SerializedName;

public  class Receipt {
    private String status;

    @SerializedName("vfdinvoicenum")
    private int invoiceNum;

    @SerializedName("rctvnum")
    private String verificationUrl;

    @SerializedName("rctvcode")
    private String receiptCode;

    @SerializedName("idate")
    private String date;

    @SerializedName("itime")
    private String time;

    public Receipt(String status, String verificationUrl, String receiptCode, int invoiceNum, String date, String time) {
        this.status = status;
        this.invoiceNum = invoiceNum;
        this.verificationUrl = verificationUrl;
        this.receiptCode = receiptCode;
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public int getInvoiceNum() {
        return invoiceNum;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }
}



