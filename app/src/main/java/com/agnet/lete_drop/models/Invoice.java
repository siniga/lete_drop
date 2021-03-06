package com.agnet.lete_drop.models;

import java.util.List;

public class Invoice {

    private  String idate, itime, custinvoiceno, custidtype, custid, custname, mobilenum, username;
    private List<InvoiceDetail> invoiceDetails;


    public  Invoice(String idate, String itime, String custinvoiceno, String custidtype, String custid, String custname, String mobilenum, String username, List<InvoiceDetail> invoiceDetails){
        this.idate = idate;
        this.itime = itime;
        this.custinvoiceno = custinvoiceno;
        this.custidtype = custidtype;
        this.custid = custid;
        this.custname = custname;
        this.mobilenum = mobilenum;
        this.username = username;
        this.invoiceDetails = invoiceDetails;
    }

    public List<InvoiceDetail> getInvoiceDetailList() {
        return invoiceDetails;
    }

    public String getCustid() {
        return custid;
    }

    public String getCustidtype() {
        return custidtype;
    }

    public String getCustinvoiceno() {
        return custinvoiceno;
    }

    public String getCustname() {
        return custname;
    }

    public String getIdate() {
        return idate;
    }

    public String getItime() {
        return itime;
    }

    public String getMobilenum() {
        return mobilenum;
    }

    public String getUsername() {
        return username;
    }


}

