package com.agnet.leteApp.models;

public class Cart {

    private int id,quantity, productId;
    private double total_amount, itemPrice;
    private String name;


    public Cart(int id,String name, double amount, int productId, int quantity, double itemPrice){
        this.id = id;
        this.total_amount = amount;
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.itemPrice = itemPrice;

    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return total_amount;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public double getItemPrice() {
        return itemPrice;
    }
}
