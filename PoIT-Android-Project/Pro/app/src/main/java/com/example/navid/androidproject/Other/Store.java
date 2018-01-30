package com.example.navid.androidproject.Other;

import android.net.Uri;

import java.util.ArrayList;

public class Store {

    private String ID;
    private String name;
    private ArrayList<Uri> images;
    private String salesmanID;
    private String slogan;
    private String description;
    private String openingDate;
    private ArrayList<Product> products;

    public Store (String ID,String name , ArrayList<Uri> images ,String salesmanID, String slogan , String description, String openingDate,ArrayList<Product> products){
        this.ID = ID;
        this.name = name;
        this.images = images;
        this.salesmanID = salesmanID;
        this.slogan = slogan;
        this.description = description;
        this.openingDate = openingDate;
        this.products = products;
    }

    public String getID() {
        return ID;
    }

    public String getName(){
        return name;
    }

    public ArrayList<Uri> getImage(){
        return images;
    }

    public String getSalesmanID() {
        return salesmanID;
    }

    public ArrayList<Product> getProduct(){
        return products;
    }

    public String getSlogan(){
        return slogan;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public String getDescription(){
        return description;
    }
}
