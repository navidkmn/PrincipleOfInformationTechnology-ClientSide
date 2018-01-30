package com.example.navid.androidproject.Other;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.example.navid.androidproject.Activity.InterestsActivity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;

public class StringArrayList {

    public static String convertArrayListToString(ArrayList input){
        String output = "";
        if(!output.isEmpty()) {
            for (int i = 0; i < input.size() - 1; i++)
                output += input.get(i).toString() + ",";
            output += input.get(input.size() - 1);
        }
        return output;
    }

    public static String convertProductArrayListToString(ArrayList<Product> input){
        String output = "";
        if(!output.isEmpty()) {
            for (int i = 0; i < input.size() - 1; i++)
                output += input.get(i).getJsonFormat() + "&";
            output += input.get(input.size() - 1).getJsonFormat();
        }
        return output;
    }

    public static ArrayList<Product> convertStringToProductArrayList(String input ,File cacheDir){
        ArrayList<Product> output = new ArrayList<>();
        String[] split = input.split("&");
        for(int i = 0 ; i < split.length ; i++) {
            try {
                ArrayList<Uri> images = new ArrayList<>();
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(split[i]);
                JSONArray[] jsonArrays = new JSONArray[10];
                jsonArrays[0] = (JSONArray) jsonObject.get("ID");
                jsonArrays[1] = (JSONArray) jsonObject.get("name");
                jsonArrays[2] = (JSONArray) jsonObject.get("price");
                jsonArrays[3] = (JSONArray) jsonObject.get("offPrice");
                jsonArrays[4] = (JSONArray) jsonObject.get("storeID");
                jsonArrays[5] = (JSONArray) jsonObject.get("storeName");
                jsonArrays[6] = (JSONArray) jsonObject.get("rate");
                jsonArrays[7] = (JSONArray) jsonObject.get("quantity");
                jsonArrays[8] = (JSONArray) jsonObject.get("description");
                jsonArrays[9] = (JSONArray) jsonObject.get("images");
                for (int j = 0; j < jsonArrays[8].size(); j++) {
                    try {
                        File file = new File(cacheDir, StringGenerator.getSaltString());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        bos.write(BankBase64.decode(jsonArrays[8].get(j).toString(), 0));
                        bos.flush();
                        bos.close();
                        Uri uri = Uri.fromFile(file);
                        images.add(uri);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                output.add(new Product(jsonArrays[0].get(0).toString(), jsonArrays[1].get(0).toString(), jsonArrays[2].get(0).toString(),jsonArrays[3].get(0).toString(),jsonArrays[4].get(0).toString(),jsonArrays[5].get(0).toString(), images , jsonArrays[6].get(0).toString(), jsonArrays[7].get(0).toString() , jsonArrays[8].get(0).toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    public static ArrayList<String> convertStringToArrayList(String input){
        Log.e("CONVERT" , input);
        ArrayList<String> output = new ArrayList<>();
        String[] split = input.split(",");
        for (int i = 0 ; i < split.length ; i++)
            output.add(split[i]);
        Log.e("CONVERT" , output.get(0));
        return output;
    }

    public static ArrayList<String> convertUriArrayListToStringArrayList(ArrayList<Uri> input , ContentResolver contentResolver){
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0 ; i < input.size() ;i++){
            try {
                InputStream inputStream = contentResolver.openInputStream(input.get(i));
                String image = BankBase64.encodeToString(IOUtils.toByteArray(inputStream), 0);
                Log.e("ConvertImage" , image);
                Log.e("COnvertImage" , Integer.toString(image.length()));
                output.add(image);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return output;
    }

    public static ArrayList<Uri> convertStringArrayListToUriArrayList(ArrayList<String> input , File cacheDir){
        ArrayList<Uri> output = new ArrayList<>();
        for (int i = 0 ; i < input.size() ; i++) {
            Log.e("FAVORITE" , input.get(0).toString());
            try{
                File file = new File(cacheDir,StringGenerator.getSaltString());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(BankBase64.decode(input.get(i) , 0));
                bos.flush();
                bos.close();
                Uri uri = Uri.fromFile(file);
                output.add(uri);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return output;
    }

    public static String getPriceIntFormat(String input){
        String price = "";
        for(int i = 0 ; i< input.length() ;i++)
            if(input.charAt(i) != ',')
                price += input.charAt(i);
        return price;
    }

    public static String getPriceFormat(String input){
        float f = Float.parseFloat(input);
        int in = (int) f;
        String temp = Integer.toString(in);
        String price = "";
        if(temp.length() > 3) {
            price += temp.charAt(0);
            for (int i = 1; i < temp.length(); i++) {
                if ((temp.length() - i) % 3 == 0) {
                    price += ",";
                    price += temp.charAt(i);
                } else
                    price += temp.charAt(i);
            }
        }
        else{
            price = temp;
        }
        return price;
    }
}
