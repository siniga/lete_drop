package com.agnet.lete_drop.models;

public class OrderResponse {
   private  History order;
   private int code;



    public OrderResponse(History order, int code){
       this.code = code;
       this.order = order;
    }

    public History getOrder() {
        return order;
    }

    public int getCode() {
        return code;
    }


}
