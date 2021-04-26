package com.tetradev.farmeroapp;

import java.util.List;

public class Request {

    private  String phone;
    private String name;
    private String address;
    private String total;
    private List<Order> products; //list of product orders

    public Request(){

    }

    public Request(

            String name,
            String address,
            String phone,
             String total, List<Order> products) {
        this.name = name;
        this.address = address;
        this.phone = phone;

        this.total = total;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getProducts() {
        return products;
    }

    public void setProducts(List<Order> products) {
        this.products = products;
    }
}
