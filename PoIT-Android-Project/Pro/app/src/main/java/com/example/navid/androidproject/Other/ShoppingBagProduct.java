package com.example.navid.androidproject.Other;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

public class ShoppingBagProduct {

    private String productID;
    private String productName;
    private String productPrice;
    private Uri productImage;
    private String productStore;
    private String productQuantity;

    public ShoppingBagProduct(String productID,String productName , String productPrice,Uri productImage,String productStore , String productQuantity){
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productStore = productStore;
        this.productQuantity = productQuantity;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName(){
        return productName;
    }

    public String getProductPrice(){
        return productPrice;
    }

    public String getProductStore(){
        return productStore;
    }

    public Uri getProductImage(){
        return productImage;
    }

    public String getProductQuantity(){
        return productQuantity;
    }

    public void setProductQuantity(String quantity){
        this.productQuantity = quantity;
    }
}
