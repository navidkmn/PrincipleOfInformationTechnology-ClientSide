package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.example.navid.androidproject.Adapter.ProductAdapterGridLayout;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Listener.EndlessRecyclerOnScrollListener;
import com.example.navid.androidproject.Listener.OnLoadMoreListener;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.TemporaryStoreProductsList;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CompleteListActivity extends AppCompatActivity {

    ImageView _backArrow;
    String completeListName;
    TextView _completeListName;
    SearchView _searchView;

    InternetConnection internetConnection;

    public ArrayList<Product> products;
    public ArrayList<Product> rvProducts;

    private ProductAdapterGridLayout productAdapterGridLayout;
    private RecyclerView productRecyclerView;
    private ProgressBar progressBar;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_list);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());
        internetConnection = new InternetConnection(this);

        completeListName = StaticInfo.SAVED_COMPLETE_LIST;
        _completeListName = (TextView) findViewById(R.id.completeListName);
        _completeListName.setText(completeListName);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String s = getIntent().getStringExtra("completeListName");
        getData(s);

        _backArrow = (ImageView) findViewById(R.id.backFromCompleteList);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _searchView = (SearchView) findViewById(R.id.searchView);
        _searchView.setIconified(true);
        _searchView.setQueryHint("جست\u200Cو\u200Cجو کنید...");
        _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.item_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(R.color.primary, PorterDuff.Mode.SRC_IN);
    }

    public void insertProduct(){
        productRecyclerView = (RecyclerView) findViewById(R.id.productRecyclerView);
        productRecyclerView.addItemDecoration(new DividerItemDecoration(CompleteListActivity.this,DividerItemDecoration.VERTICAL));
        RecyclerView.LayoutManager prodcutLayoutManager =  new GridLayoutManager(this , 2);
        productRecyclerView.setLayoutManager(prodcutLayoutManager);
        productAdapterGridLayout = new ProductAdapterGridLayout(getApplicationContext() , rvProducts , this);
        productRecyclerView.setAdapter(productAdapterGridLayout);

        productRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int last = rvProducts.size();
                        int end = last + 6;
                        for(int i = last; i<end ; i++) {
                            if (i < products.size()) {
                                rvProducts.add(products.get(i));
                            }
                        }
                        productAdapterGridLayout.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void openNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra("completeListName" , completeListName);
            intent.putExtra("pre" , 3);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            intent.putExtra("completeListName" , completeListName);
            intent.putExtra("pre" , 3);
            intent.putExtra("activity" , 2);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    public ArrayList<Uri> getImageUri(String[] images){
        ArrayList<Uri> imagesUri = new ArrayList<>();
        for(int i = 0 ; i < images.length ;i++){
            if(images[i] != ""){
                try{
                    File file = new File(getCacheDir(), StringGenerator.getSaltString());
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

    public void getProductsFromServer(String input){
        try{
            JSONParser jsonParser = new JSONParser();
            String decodeContent = "";
            try{
                AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123" , "arsham");
                AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(input);
                decodeContent = AesCbcWithIntegrity.decryptString(value,key);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONArray jsonArray = (JSONArray) jsonParser.parse(decodeContent);

            for(int i = 0 ; i < jsonArray.size() ; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String productID = jsonObject.get("id").toString();
                String productName = jsonObject.get("name").toString();
                String productPrice = StringArrayList.getPriceFormat(jsonObject.get("price").toString());
                String productOffPrice = jsonObject.get("offPrice").toString();
                String productStoreID = jsonObject.get("parlorID").toString();
                String productStore = jsonObject.get("parlorName").toString();
                String productSubCategory = jsonObject.get("subcategoryName").toString();
                String productQuantity =jsonObject.get("quantity").toString();
                String productDescription = jsonObject.get("description").toString();
                String[] images = new String[5];
                try {
                    images[0] = jsonObject.get("image1").toString();
                }
                catch (Exception e){
                    images[0] = "";
                }
                try {
                    images[1] = jsonObject.get("image2").toString();
                }
                catch (Exception e){
                    images[1] = "";
                }
                try {
                    images[2] = jsonObject.get("image3").toString();
                }
                catch (Exception e){
                    images[2] = "";
                }
                try {
                    images[3] = jsonObject.get("image4").toString();
                }
                catch (Exception e){
                    images[3] = "";
                }
                try {
                    images[4] = jsonObject.get("image5").toString();
                }
                catch (Exception e){
                    images[4] = "";
                }
                products.add(new Product(productID,productName,productPrice,productOffPrice,productStoreID,productStore,getImageUri(images),productQuantity,productDescription,productSubCategory));
            }
           // StaticInfo.SAVED_PRODUCTS = products;
            for(int i = 0 ; i < 6 ; i++){
                if(products.size() > i)
                    rvProducts.add(products.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSearchData(String query) {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/findProduct.do?" + "keyword="+ query;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("0")){
                    Toast.makeText(getApplicationContext(),"کالایی یافت نشد",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    products = new ArrayList<>();
                    rvProducts = new ArrayList<>();
                    getProductsFromServer(response);
                    insertProduct();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getData(String s) {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixteen" + s + "Products.do";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    if(response.equals("0")){
                        Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else{
                        products = new ArrayList<Product>();
                        rvProducts = new ArrayList<Product>();
                        getProductsFromServer(response);
                        insertProduct();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
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

//    public class Search extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/findProduct.do?" +
//                        "keyword="+params[0].toString());
//
//                Log.e("Mamad",url.toString());
//
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
//
//            if (o != null) {
//                String content = o.toString();
//                if(o.equals("0")){
//                    Toast.makeText(getApplicationContext(),"کالایی یافت نشد",Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    products = new ArrayList<>();
//                    rvProducts = new ArrayList<>();
//                    getProductsFromServer(o.toString());
//                    insertProduct();
//                }
//            } else {
//                Toast.makeText(getApplicationContext(),"کالایی یافت نشد",Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//        }
//    }

//    public class GetAllProducts extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getAllProducts.do");
//
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
//
//            if (o != null) {
//                String content = o.toString();
//                if(o.equals("0")){
//                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    products = new ArrayList<Product>();
//                    rvProducts = new ArrayList<Product>();
//                    getProductsFromServer(o.toString());
//                    insertProduct();
//                }
//            } else {
//                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//        }
//    }
}
