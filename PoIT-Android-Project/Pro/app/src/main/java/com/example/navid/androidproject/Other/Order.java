package com.example.navid.androidproject.Other;

import android.net.Uri;

public class Order {

    String productName;
    String price;
    String offPrice;
    String storeName;
    Uri image;
    String quantity;
    String orderStatus;
    String submitDate;

    public Order(String productName, String price, String offPrice, String storeName, Uri image, String quantity, String orderStatus, String submitDate){
        this.productName = productName;
        this.price = price;
        this.offPrice = offPrice;
        this.storeName = storeName;
        this.image = image;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
        this.submitDate = submitDate;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public String getOffPrice() {
        return offPrice;
    }

    public String getStoreName() {
        return storeName;
    }

    public Uri getImage() {
        return image;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getSubmitDate() {
        return submitDate;
    }
}
