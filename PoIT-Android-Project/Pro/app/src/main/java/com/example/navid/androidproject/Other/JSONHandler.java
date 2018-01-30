package com.example.navid.androidproject.Other;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONHandler {

    public JSONArray convert_toJson(String[] personalInformation){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        if(personalInformation.length == 2){
            jsonObject.put("mailAddress",personalInformation[0]);
            jsonObject.put("passCode",personalInformation[1]);

            jsonArray.add(jsonObject);
        }
        if(personalInformation.length == 7){
            jsonObject.put("firstName",personalInformation[0]);
            jsonObject.put("lastName",personalInformation[1]);
            jsonObject.put("sexID",Integer.parseInt(personalInformation[2]));
            jsonObject.put("mailAddress",personalInformation[3]);
            jsonObject.put("phoneNumber",Long.parseLong(personalInformation[4]));
            jsonObject.put("passCode",personalInformation[5]);
            jsonObject.put("RegisteredDate",personalInformation[6]);

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
