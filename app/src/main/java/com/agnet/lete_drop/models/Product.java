package com.agnet.lete_drop.models;

import com.google.gson.annotations.SerializedName;

public class Product {

    int id,skuId;
    String name,  discount, quantity,price;

    @SerializedName("photo")
    String imgUrl;

    @SerializedName("category_id")
    int categoryId;


    String unit, category, sku;

    public Product(int id, String name, String  price, String unit, String category, String sku, String discount, String qnty, String imgUrl, int categoryId, int skuId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.category = category;
        this.sku = sku;
        this.imgUrl = imgUrl;
        this.categoryId = categoryId;
        this.skuId = skuId;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
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
}

