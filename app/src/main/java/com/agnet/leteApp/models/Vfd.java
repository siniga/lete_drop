package com.agnet.leteApp.models;

import java.util.List;

public class Vfd {

    private  List<Invoice> invoice;


    public Vfd(List<Invoice> invoice){

        this.invoice =  invoice;

    }

    public List<Invoice> getInvoice() {
        return invoice;
    }
}
