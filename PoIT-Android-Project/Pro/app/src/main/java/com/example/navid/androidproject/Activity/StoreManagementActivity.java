package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import com.example.navid.androidproject.Adapter.ProductAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StoreManagementActivity extends AppCompatActivity {

    static final int CREATION_REQUEST = 1;
//    public ImageView _backArrow;
    public FloatingActionButton floatingActionButton;
    public Button _save;
    String preActivity;

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_management);

        preActivity = getIntent().getStringExtra("activity");
        // if preActivity is Main get products and storeName from server
        // if preActivity is StoreCreation do nothing

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        if(StaticInfo.MY_PRODUCTS == null)
            getData();
        else{
            productList = StaticInfo.MY_PRODUCTS;
            preparePage();
        }

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProductCreationActivity.class);
                startActivityForResult(intent, CREATION_REQUEST);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _save = (Button) findViewById(R.id.save);
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticInfo.MY_PRODUCTS = productList;
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                intent.putExtra("beforeStore" , "storeManagement");
                intent.putExtra("beforeHome" , "storeManagement");
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        StaticInfo.MY_PRODUCTS = productList;
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        intent.putExtra("beforeStore" , "storeManagement");
        intent.putExtra("beforeHome" , "storeManagement");
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CREATION_REQUEST)
            if(resultCode == RESULT_OK){
                String[] info = data.getStringArrayExtra("productInfo");
                ArrayList<Uri> images = new ArrayList<>();
                try{
                    Uri uri = Uri.fromFile(new File(info[6]));
                    images.add(uri);
                } catch (Exception e){
                    e.printStackTrace();
                }
                productList.add(new Product(info[0],info[1],info[2],info[3],info[4],info[5],images,info[7],info[8],info[9]));
                productAdapter.notifyItemInserted(productList.size() - 1);
            }
            if(resultCode == RESULT_CANCELED){
                //TO DO
            }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getData() {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getProductsOfParlor.do?" + "parlorID="+ User.STORE_ID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    productList = getProductsFromServer(response);
                    preparePage();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public ArrayList<Uri> getImageUri(String[] images){

        ArrayList<Uri> imagesUri = new ArrayList<>();

        for(int i = 0 ; i < images.length ;i++){
            if(images[i] != ""){
                try{
                    File file = new File(getApplicationContext().getCacheDir(),StringGenerator.getSaltString());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(BankBase64.decode(images[i] , 0));
                    bos.flush();
                    bos.close();
                    Uri uri = Uri.fromFile(file);
                    imagesUri.add(uri);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return imagesUri;
    }

    public ArrayList getProductsFromServer(String input){
        ArrayList<Product> array = new ArrayList<>();
        try {
            JSONParser jsonParser = new JSONParser();
            String decodeContent = "";
                try {
                    AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123", "arsham");
                    AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(input);
                    decodeContent = AesCbcWithIntegrity.decryptString(value, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("Majid" , decodeContent);

                JSONArray jsonArray = (JSONArray) jsonParser.parse(decodeContent);

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    String productID = jsonObject.get("id").toString();
                    String productName = jsonObject.get("name").toString();
                    String productPrice = StringArrayList.getPriceFormat(jsonObject.get("price").toString());
                    String productOffPrice = jsonObject.get("offPrice").toString();
                    String productStoreID = jsonObject.get("parlorID").toString();
                    String productStore = jsonObject.get("parlorName").toString();
                    String productSubCategory = jsonObject.get("subcategoryName").toString();
                    String productQuantity = jsonObject.get("quantity").toString();
                    String productDescription = jsonObject.get("description").toString();
                    String[] images = new String[5];
                    try {
                        images[0] = jsonObject.get("image1").toString();
                    } catch (Exception e) {
                        images[0] = "";
                    }
                    try {
                        images[1] = jsonObject.get("image2").toString();
                    } catch (Exception e) {
                        images[1] = "";
                    }
                    try {
                        images[2] = jsonObject.get("image3").toString();
                    } catch (Exception e) {
                        images[2] = "";
                    }
                    try {
                        images[3] = jsonObject.get("image4").toString();
                    } catch (Exception e) {
                        images[3] = "";
                    }
                    try {
                        images[4] = jsonObject.get("image5").toString();
                    } catch (Exception e) {
                        images[4] = "";
                    }
                    array.add(new Product(productID, productName, productPrice, productOffPrice, productStoreID, productStore, getImageUri(images), productQuantity, productDescription, productSubCategory));
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        return array;
    }


    public void preparePage(){
        productRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);
        productAdapter = new ProductAdapter(getApplicationContext() , productList , this);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.HORIZONTAL ,true));
        productRecyclerView.setAdapter(productAdapter);
    }

    public class Products extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getProductsOfParlor.do?" +
                        "parlorID="+ User.STORE_ID);

                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                int ascii = inputStream.read();
                String content = "";
                while (ascii != -1) {
                    content += (char) ascii;
                    ascii = inputStream.read();
                }
                inputStream.close();
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            if (o != null) {
                String content = o.toString();
                if(o.equals("0")){
                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("ENTER" , "   Navid");
                    if(!content.equals("")) {
                        productList = getProductsFromServer(content);
                        preparePage();
                    }
                }
            } else {
            }
        }
    }
}
