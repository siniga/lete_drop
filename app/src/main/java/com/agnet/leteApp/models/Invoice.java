package com.agnet.leteApp.models;

import java.util.List;

public class Invoice {

    private  String idate, itime, custinvoiceno, custid, custname, mobilenum, username;
    private List<InvoiceDetail> invoiceDetails;
   private  int custidtype;

    public  Invoice(String idate, String itime, String custinvoiceno, int custidtype, String custid, String custname, String mobilenum, String username, List<InvoiceDetail> invoiceDetails){
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

    public List<InvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
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

