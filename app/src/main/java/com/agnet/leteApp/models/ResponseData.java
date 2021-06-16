package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

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

    List<Outlet> outlets;

    Customer customer;

    Outlet outlet;

    int code;

    String flag;

    String token;

    Success success;

    List<Project> projects;

    List<Form> forms;

    Form form;


    public ResponseData(List streets, List products, List categories, List skus, List<History> orders,
                        List<Partner> partners, User user, Customer customer,
                        int code, String flag, Outlet outlet, String token, Success success, List<Project> projects,
                        List<Form> forms, Form form, List<Quesionnaire> questions, List<Outlet> outlets) {
        this.streets = streets;
        this.products = products;
        this.categories = categories;
        this.skus = skus;
        this.orders = orders;
        this.partners = partners;
        this.user = user;
        this.customer = customer;
        this.code = code;
        this.flag = flag;
        this.outlet = outlet;
        this.token = token;
        this.success = success;
        this.projects = projects;
        this.forms = forms;
        this.form = form;
        this.outlets = outlets;

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

    public String getFlag() {
        return flag;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public String getToken() {
        return token;
    }

    public Success getSuccess() {
        return success;
    }


    public List<Project> getProjects() {
        return projects;
    }

    public List<Form> getForms() {
        return forms;
    }


    public Form getForm() {
        return form;
    }

    public List<Outlet> getOutlets() {
        return outlets;
    }
}
