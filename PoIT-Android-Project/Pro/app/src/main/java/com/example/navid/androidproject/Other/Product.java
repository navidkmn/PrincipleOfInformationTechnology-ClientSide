package com.example.navid.androidproject.Other;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class Product {

    private String ID;
    private String name;
    private String price;
    private String offPrice;
    private String storeID;
    private String storeName;
    private ArrayList<Uri> images;
    private String quantity;
    private String description;
    private String subCategoryName;

    public Product (String ID,String name ,String price,String offPrice,String storeID,String storeName, ArrayList<Uri> images,String quantity ,String description , String subCategoryName){
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.offPrice = offPrice;
        this.storeID = storeID;
        this.storeName = storeName;
        this.images = images;
        this.quantity = quantity;
        this.description = description;
        this.subCategoryName = subCategoryName;
    }

    public String getID(){
        return ID;
    }

    public String getName(){
        return name;
    }

    public String getPrice(){
        return price;
    }

    public String getOffPrice() {
        return offPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription(){
        return  description;
    }

    public String getStoreID() {
        return storeID;
    }

    public String getStoreName(){
        return storeName;
    }

    public ArrayList<Uri> getImage(){
        return images;
    }

    public void setImage(ArrayList<Uri> images){
        this.images = images;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getJsonFormat(){
        String json = "{";
        String proID = "\"ID\":[" + "\""+ID+"\"" + "],";
        String proName = "\"name\":[" + "\""+name+"\"" + "],";
        String proPrice = "\"price\":[" + "\""+price +"\""+ "],";
        String proOffPrice = "\"offPrice\":[" + "\""+offPrice +"\""+ "],";
        String proStoreID = "\"storeID\":[" + "\""+storeID+"\"" + "],";
        String proStore = "\"storeName\":[" + "\""+storeName+"\"" + "],";
        String proImages = "\"images\":[";
        String proQuantity = "\"quantity\":[" + "\""+quantity +"\""+ "],";
        String proDescription = "\"price\":[" + "\""+description +"\""+ "],";
        String proSubCategoryName = "\"subCategoryName\":[" + "\""+subCategoryName +"\""+ "],";
        for (int i=0 ; i < images.size() - 1 ; i++) {
            proImages += "\"";
            proImages += images.get(i).toString();
            proImages += "\"";
        }
        proImages += "\"";
        proImages += images.get(images.size() - 1).toString();
        proImages += "\"";
        proImages += "]";
        String result = proName + proPrice + proOffPrice + proStoreID + proStore + proImages + proQuantity + proDescription + subCategoryName;
        json+= result;
        json+= "}";
        return json;
    }
}
