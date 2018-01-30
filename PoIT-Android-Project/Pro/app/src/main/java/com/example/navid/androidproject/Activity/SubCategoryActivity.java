package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.ReferenceQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import com.example.navid.androidproject.Adapter.SubCategoryListAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.Other.CategoryEnum;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.SubCategory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navid.androidproject.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SubCategoryActivity extends AppCompatActivity {

    private TextView categoryName;
    private ImageView _backArrow;

    private ListView listView;
    private ArrayList<SubCategory> subCategories;
    private SubCategoryListAdapter adapter;

    private int categoryID;

    private InternetConnection internetConnection;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        internetConnection = new InternetConnection(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        categoryID = categoryToID(StaticInfo.SAVED_CATEGORY.getName());

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        subCategories = new ArrayList<>();

        sendMessage();

//        SendMessage sendMessage = new SendMessage();
//        sendMessage.execute();

        categoryName = (TextView) findViewById(R.id.catName);
        categoryName.setText(StaticInfo.SAVED_CATEGORY.getName());

        _backArrow = (ImageView) findViewById(R.id.backFromSubcategory);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    if(internetConnection.haveNetworkConnection()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment" , "category");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void createAdapter(){

        listView = (ListView) findViewById(R.id.subCatList);
        adapter = new SubCategoryListAdapter(getApplicationContext() , subCategories);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StaticInfo.SAVED_SUBCATEGORY_ID = CategoryEnum.getSubCategoryID(subCategories.get(position).name);
                StaticInfo.SAVED_SUBCATEGORY = subCategories.get(position).name;
                if (internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), SubSubCategoryActivity.class);
//                    intent.putExtra("categoryName" , getIntent().getStringExtra("categoryName"));
//                    intent.putExtra("subCategoryName" , subCategories.get(position).name);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                  //  intent.putExtra("categoryName" , getIntent().getStringExtra("categoryName"));
                    intent.putExtra("activity" , 6);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
    }

    public void getSubCategories(String s){
        try {
            JSONParser jsonParser = new JSONParser();
            String decodeContent = "";
            try{
                AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123" , "arsham");
                AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(s);
                decodeContent = AesCbcWithIntegrity.decryptString(value,key);
            }catch (Exception e){
                e.printStackTrace();
            }
            JSONArray jsonarray = (JSONArray) jsonParser.parse(decodeContent);
            for (int i = 0; i < jsonarray.size(); i++) {
                JSONObject jsonobject = (JSONObject) jsonarray.get(i);
                String name = jsonobject.get("name").toString();
                String id = jsonobject.get("id").toString();
                subCategories.add(new SubCategory(Integer.parseInt(id), name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("fragment" , "category");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private int categoryToID(String category){
        int id;
        switch (category){
            case "کالای دیجیتال":
                id = 1;
                break;
            case "مد و لباس":
                id = 2;
                break;
            case "لوازم خانه":
                id = 3;
                break;
            case "زیبایی و سلامت":
                id = 4;
                break;
            case "سرگرمی و ورزش":
                id = 5;
                break;
            case "فرهنگ و هنر":
                id = 6;
                break;
            case "ابزار و خودرو":
                id = 7;
                break;
            case "مواد غذایی و خوراکی":
                id = 8;
                break;
            default:
                id = -1;
                break;
        }
        return id;
    }
    public void sendMessage() {
        String url = "http://"+ Server.IP+":"+Server.PORT+"/category.do?pk=" + Integer.toString(categoryID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                  getSubCategories(response);
                    createAdapter();
                } else{
                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        requestQueue.add(stringRequest);
    }
//    public class SendMessage extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                URL url = new URL("http://"+ Server.IP+":"+Server.PORT+"/category.do?pk=" + Integer.toString(categoryID));
//                URLConnection urlConnection = url.openConnection();
//                InputStream inputStream = urlConnection.getInputStream();
//                int ascii = inputStream.read();
//                String content = "";
//                while (ascii != -1) {
//                    content += (char) ascii;
//                    ascii = inputStream.read();
//                }
//                inputStream.close();
//                return content;
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            if (o != null) {
//                try {
//                    String content = o.toString();
//                    try {
//                        JSONParser jsonParser = new JSONParser();
//                        String decodeContent = "";
//                        try{
//                            AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123" , "arsham");
//                            AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(content);
//                            decodeContent = AesCbcWithIntegrity.decryptString(value,key);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        JSONArray jsonarray = (JSONArray) jsonParser.parse(decodeContent);
//                        for (int i = 0; i < jsonarray.size(); i++) {
//                            JSONObject jsonobject = (JSONObject) jsonarray.get(i);
//                            String name = jsonobject.get("name").toString();
//                            String id = jsonobject.get("id").toString();
//                            subCategories.add(new SubCategory(Integer.parseInt(id), name));
//                        }
//                        createAdapter();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }catch (Exception e){
//
//                }
//
//            }
//            else{
//                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
