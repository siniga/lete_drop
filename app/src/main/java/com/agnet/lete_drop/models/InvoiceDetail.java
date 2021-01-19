package com.agnet.lete_drop.models;

public class InvoiceDetail {

    private String description, taxcode,amt,qty;

    public InvoiceDetail(String description, String qty, String taxcode, String amt){
        this.description = description;
        this.qty = qty ;
        this.taxcode = taxcode;
        this.amt = amt;
    }

    public String getAmt() {
        return amt;
    }

    public String getDescription() {
        return description;
    }

    public String getQnty() {
        return qty;
    }

    public String getTaxcode() {
        return taxcode;
    }
}
