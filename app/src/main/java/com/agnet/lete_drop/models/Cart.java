package com.agnet.lete_drop.models;

public class Cart {

    int id,quantity,totalPrice, serverId, orderId;
    String name,imgUrl, originalPrice, sku;


    public Cart(int id, int serverId, int totalPrice, int quantity, String name, String imgUrl, String originalPrice, String sku, int orderId){
        this.id = id;
        this.serverId = serverId;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.name = name;
        this.imgUrl = imgUrl;
        this.originalPrice = originalPrice;
        this.sku = sku;
        this.orderId = orderId;

    }

    public int getId() {
        return id;
    }

    public int getServerId() {
        return serverId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getSku() {
        return sku;
    }

    public int getOrderId() {
        return orderId;
    }
}
