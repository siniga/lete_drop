package com.agnet.lete_drop.models;

import java.util.List;

public class ResponseData {
    User user;

    List<Street> streets;

    List<Customer> customers;

    // @SerializedName("data")
    List<Product> products;

    List<Category> categories;

    List<Sku> skus;

    List<History> orders;

    List<Partner> partners;

    Customer customer;

    int code;

    public ResponseData(List streets, List products, List outlets, List categories, List skus, List<History> orders, List<Partner> partners, User user, Customer customer, int code) {
        this.streets = streets;
        this.products = products;
        this.customers = outlets;
        this.categories = categories;
        this.skus = skus;
        this.orders = orders;
        this.partners = partners;
        this.user = user;
        this.customer = customer;
        this.code = code;
    }


    public List<Partner> getPartners() {
        return partners;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public List<History> getOrders() {
        return orders;
    }

    public User getUser() {
        return user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getCode() {
        return code;
    }
}
