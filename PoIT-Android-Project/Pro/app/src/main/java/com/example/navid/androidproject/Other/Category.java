package com.example.navid.androidproject.Other;

public class Category {
    String name;
    int image;
    public Category(String name , int image){
        this.name = name;
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public int getImage(){
        return image;
    }
}
