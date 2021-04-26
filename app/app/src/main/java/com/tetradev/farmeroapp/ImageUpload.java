package com.tetradev.farmeroapp;

public class ImageUpload {

    public String name;
    public String desc;
    public String price;
    public String contact;
    public String url;

    public String getName() {
        return name;
    }

    public String getDesc(){
        return desc;
    }

    public String getPrice(){
        return price;
    }

    public String getContact() {
        return contact;
    }

    public String getUrl() {
        return url;
    }

    public ImageUpload(String name, String desc, String price,String contact, String url) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.contact = contact;
        this.url = url;
    }
    public ImageUpload(){}




}
