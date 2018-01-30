package com.example.navid.androidproject.Other;

public class Comment {

    public String sender;
    public String description;
    public String submitdate;
    public String es;
    public String qs;

    public Comment(String sender , String description , String submitdate , String es , String qs){
        this.sender = sender;
        this.description = description;
        this.submitdate = submitdate;
        this.es = es;
        this.qs = qs;
    }

    public String getSender(){
        return sender;
    }

    public String getDescription(){
        return description;
    }

    public String getSubmitdate() {
        return submitdate;
    }

    public String getEs() {
        return es;
    }

    public String getQs() {
        return qs;
    }
}
