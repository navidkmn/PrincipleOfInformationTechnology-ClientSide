package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.example.navid.androidproject.Adapter.OrderAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Listener.EndlessRecyclerOnScrollListener;
import com.example.navid.androidproject.Other.Order;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navid.androidproject.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MyOrderActivity extends AppCompatActivity {

    ImageView _backArrow;

    OrderAdapter orderAdapter;
    RecyclerView recyclerView;

    ArrayList<Order> orders;
    ArrayList<Order> rvOrderList;
    private ProgressBar progressBar;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        progressBar = (ProgressBar) findViewById(R.id.item_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(R.color.primary, PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        orders = new ArrayList<>();
        rvOrderList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        getData();

        _backArrow = (ImageView) findViewById(R.id.backFromOrder);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK , intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void getData(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getOrderedList.do?customerID=" + User.CUSTOMER_ID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    getOrders(response);
                    doNext();
                }
                else {
                    Toast.makeText(getApplicationContext(),"خطا", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getOrders(String input){
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

            Log.e("DecodeContent" , decodeContent);

            JSONArray jsonArray = (JSONArray) jsonParser.parse(decodeContent);

            for(int i = 0 ; i < jsonArray.size() ; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String productName = jsonObject.get("productName").toString();
                String productID = jsonObject.get("productID").toString();
                String quantity = jsonObject.get("quantity").toString();
                String price = StringArrayList.getPriceFormat(jsonObject.get("price").toString());
                String offPrice = StringArrayList.getPriceFormat(jsonObject.get("offPrice").toString());
                String storeName = jsonObject.get("parlorName").toString();
                String image = jsonObject.get("image1").toString();
                String orderStatus = jsonObject.get("orderStatus").toString();
                String submitDate = jsonObject.get("submitDate").toString();

                Uri uri = null;

                try{
                    File file = new File(getApplicationContext().getCacheDir(), StringGenerator.getSaltString());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(BankBase64.decode(image , 0));
                    bos.flush();
                    bos.close();
                    uri = Uri.fromFile(file);
                }catch (Exception e){
                    e.printStackTrace();
                }

                String order = "";
                switch (orderStatus){
                    case "0":
                        order = "در انتظار تائید";
                        break;
                    case "1":
                        order = "تائید شده";
                        break;
                    case "2":
                        order = "تحویل داده‌شده";
                        break;
                    default:
                        order = "دارای مشکل";
                        break;
                }
                orders.add(new Order(productName,price,offPrice,storeName,uri, quantity,order,submitDate));
            }

            for(int i = 0 ; i < 10 ; i++)
                if(i < orders.size())
                    rvOrderList.add(orders.get(i));

            if(orders.size() == 0)
                progressBar.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doNext(){
        recyclerView = (RecyclerView) findViewById(R.id.productRV);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        orderAdapter = new OrderAdapter(getApplicationContext(), rvOrderList, this);
        recyclerView.setAdapter(orderAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int last = rvOrderList.size();
                        int end = last + 10;
                        for(int i = last; i<end ; i++) {
                            if (i < orders.size()) {
                                rvOrderList.add(orders.get(i));
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK , intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
