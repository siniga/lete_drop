package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Product {

   private int id,skuId;
    private String name,  discount, quantity;

    @SerializedName("price")
    private Double price;

    @SerializedName("img")
    String imgUrl;

    @SerializedName("category_id")
    int categoryId;

    int active;


    String unit, category, sku;

    public Product(int id, String name, Double  price, String unit, String category, String sku, String discount, String qnty, String imgUrl, int categoryId, int skuId, int active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.category = category;
        this.sku = sku;
        this.imgUrl = imgUrl;
        this.categoryId = categoryId;
        this.skuId = skuId;
        this.active = active;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getCategory() {
        return category;
    }

    public String getSku() {
        return sku;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUnit() {
        return unit;
    }

    public String  getQuantity() {
        return quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getSkuId() {
        return skuId;
    }

    public int getActive() {
        return active;
    }
}

