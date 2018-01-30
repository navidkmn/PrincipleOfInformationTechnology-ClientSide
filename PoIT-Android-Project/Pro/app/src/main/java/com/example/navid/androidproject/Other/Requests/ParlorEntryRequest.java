package com.example.navid.androidproject.Other.Requests;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import com.example.navid.androidproject.Other.Server;

/**
 * Created by navid on 04/12/2017.
 */

public class ParlorEntryRequest extends SpringAndroidSpiceRequest<String> {
    private String request;

    public ParlorEntryRequest(String request){
        super(String.class);
        this.request = request;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        String url = String.format("http://" + Server.IP + ":" + Server.PORT + "/" , request);
        return getRestTemplate().getForObject(url,String.class);
    }

    public String createCacheKey() {
        return "temp." + request;
    }
}
