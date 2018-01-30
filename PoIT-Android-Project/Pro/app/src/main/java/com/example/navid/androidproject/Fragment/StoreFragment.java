package com.example.navid.androidproject.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.NoConnectionActivity;
import com.example.navid.androidproject.Activity.StoreActivity;
import com.example.navid.androidproject.Activity.StoreCompleteListActivity;
import com.example.navid.androidproject.Adapter.BanerViewPagerAdapter;
import com.example.navid.androidproject.Adapter.CategoryImageAdapter;
import com.example.navid.androidproject.Adapter.StoreAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.Store;
import com.example.navid.androidproject.Adapter.StoreAdapter;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.TemporaryStoreProductsList;

public class StoreFragment extends Fragment{

    private ViewPager banerViewPager;
    private BanerViewPagerAdapter banerViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private ArrayList<Uri> banerImages;

    private RecyclerView bestRecyclerView;
    private RecyclerView popularRecyclerView;
    private RecyclerView newestRecyclerView;

    private StoreAdapter bestAdapter;
    private StoreAdapter popularAdapter;
    private StoreAdapter newestAdapter;

    private ArrayList<Store> bestStoreList;
    private ArrayList<Store> popularStoreList;
    private ArrayList<Store> newestStoreList;

    private InternetConnection internetConnection;

    private SwipeRefreshLayout refresh;

    private TextView storeTitle;

    RequestQueue newQueue,popularQueue,bestQueue;

    View myView;

    private static int TOTAL_RETRIES = 100;
    private int retryCount = 0;

    private String title;
    private int page;

//    public static StoreFragment newInstance(int page, String title) {
//        StoreFragment storeFragment = new StoreFragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        storeFragment.setArguments(args);
//        return storeFragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        page = getArguments().getInt("someInt", 0);
//        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_store, container, false);

        newQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        popularQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        bestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        internetConnection = new InternetConnection(this.getActivity());

        banerImages = new ArrayList<Uri>();

//        NewestStores newestStores = new NewestStores();
//        newestStores.execute();

        if(getActivity().getIntent().getStringExtra("beforeStore") != null) {
            getActivity().getIntent().removeExtra("beforeStore");
            newQueue.getCache().remove("http://" + Server.IP + ":" + Server.PORT + "/getSixNewestParlors.do");
        }
        getNewData();
        getPopularData();
       // get‌BestData();

        banerViewPager = (ViewPager) myView.findViewById(R.id.baner);
        banerViewPager.setFocusable(true);
        dotsLayout = (LinearLayout) myView.findViewById(R.id.banerDots);

        banerViewPager.setCurrentItem(banerImages.size() - 1);
        banerViewPager.setRotationY(180);

        addBottomDots(banerImages.size() - 1);

        banerViewPagerAdapter = new BanerViewPagerAdapter(getContext() , banerImages);
        banerViewPager.setAdapter(banerViewPagerAdapter);
        banerViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        ////////////////////////////
       // TextView text = (TextView) myView.findViewById(R.id.comList1);
        TextView text2 = (TextView) myView.findViewById(R.id.comList2);
        TextView text3 = (TextView) myView.findViewById(R.id. comList3);

        SpannableString ss = new SpannableString("لیست کامل");
        ss.setSpan(new UnderlineSpan(), 0 , ss.length() , 0);
      //  text.setText(ss);
        text2.setText(ss);
        text3.setText(ss);

//        text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(internetConnection.haveNetworkConnection()){
//                    Intent intent = new Intent(getActivity().getApplicationContext() , StoreCompleteListActivity.class);
//                    StaticInfo.SAVED_STORE_COMPLETE_LIST = "بهترین‌ها (قیمت)";
//                    intent.putExtra("completeListName" , "Tops");
//                    getActivity().finish();
//                    startActivity(intent);
//                }
//                else{
//                    Intent intent = new Intent(getContext(), NoConnectionActivity.class);
//                    StaticInfo.SAVED_STORE_COMPLETE_LIST = "بهترین‌ها (قیمت)";
//                    intent.putExtra("completeListName" , "Tops");
//                    intent.putExtra("activity" , 11);
//                    startActivity(intent);
//                    getActivity().finish();
//                }
//            }
//        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()){
                    Intent intent = new Intent(getActivity().getApplicationContext() , StoreCompleteListActivity.class);
                    StaticInfo.SAVED_STORE_COMPLETE_LIST = "محبوب‌ترین‌ها";
                    intent.putExtra("completeListName" , "Favs");
                    getActivity().finish();
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getContext(), NoConnectionActivity.class);
                    StaticInfo.SAVED_STORE_COMPLETE_LIST = "محبوب‌ترین‌ها";
                    intent.putExtra("completeListName" , "Favs");
                    intent.putExtra("activity" , 11);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()){
                    Intent intent = new Intent(getActivity().getApplicationContext() , StoreCompleteListActivity.class);
                    StaticInfo.SAVED_STORE_COMPLETE_LIST = "جدیدترین‌ها";
                    intent.putExtra("completeListName" , "Newest");
                    getActivity().finish();
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getContext(), NoConnectionActivity.class);
                    StaticInfo.SAVED_STORE_COMPLETE_LIST = "جدیدترین‌ها";
                    intent.putExtra("completeListName" , "Newest");
                    intent.putExtra("activity" , 11);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        refresh = (SwipeRefreshLayout) myView.findViewById(R.id.storeRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixNewestParlors.do",true);
                popularQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixFavsParlors.do", true);
                //bestQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixTopsParlors.do", true);
                getPopularData();
                getNewData();
                //get‌BestData();
            }
        });

        return myView;
    }

    public void doBest(){
        bestRecyclerView = (RecyclerView) myView.findViewById(R.id.bestSellingRecyclerView);
        bestAdapter = new StoreAdapter(myView.getContext() , bestStoreList , this);
        bestRecyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext() , LinearLayoutManager.HORIZONTAL ,true));
        bestRecyclerView.setAdapter(bestAdapter);
    }

    public void doPopular(){
        popularRecyclerView = (RecyclerView) myView.findViewById(R.id.mostPopularRecyclerView);
        popularAdapter = new StoreAdapter(myView.getContext() , popularStoreList , this);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext() , LinearLayoutManager.HORIZONTAL , true));
        popularRecyclerView.setAdapter(popularAdapter);
    }

    public void doNew(){
        newestRecyclerView = (RecyclerView) myView.findViewById(R.id.newestRecyclerView);
        newestAdapter = new StoreAdapter(myView.getContext() , newestStoreList , this);
        newestRecyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext() , LinearLayoutManager.HORIZONTAL , true));
        newestRecyclerView.setAdapter(newestAdapter);
    }

    /*
      Baner functions
   */
    private void addBottomDots(int currentPage) {
        dots = new TextView[banerImages.size()];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0 ; i < dots.length ; i++) {
            dots[i] = new TextView(myView.getContext());
            dots[i].setText("\u2022");
            dots[i].setTextSize(30);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return banerViewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(banerImages.size() - 1 - position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void get‌BestData(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixTopsParlors.do";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("BEstdata", response);
                refresh.setRefreshing(false);
                bestStoreList = getStoresFromServer(response);
                doBest();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                //Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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
        bestQueue.add(stringRequest);
    }

    private void getPopularData(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixFavsParlors.do";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("BEstPOP", response);
                refresh.setRefreshing(false);
                popularStoreList = getStoresFromServer(response);
                doPopular();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                //Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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
        popularQueue.add(stringRequest);
    }

    private void getNewData() {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixNewestParlors.do";

        Log.e("MMin", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("NEWdata", response);
                refresh.setRefreshing(false);
                newestStoreList = getStoresFromServer(response);
                doNew();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                //Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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
        newQueue.add(stringRequest);
    }

    public void openNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getContext(), StoreActivity.class);
            intent.putExtra("pre" , 1);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getContext(), NoConnectionActivity.class);
            intent.putExtra("pre" , 1);
            intent.putExtra("activity" , 4);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    public ArrayList getStoresFromServer(String input){

        ArrayList<Store> array = new ArrayList<>();
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

                String storeID = jsonObject.get("id").toString();
                String storeName = jsonObject.get("name").toString();
                String storeSlogan = jsonObject.get("slogan").toString();
                storeSlogan = storeSlogan.replace('_',' ');
                String storeSalesmanID = jsonObject.get("salesmanID").toString();
                String storeOpeningDate =jsonObject.get("openingDate").toString();
                String storeDescription = jsonObject.get("description").toString();
                String image;
                try{
                    image = jsonObject.get("poster").toString();
                }catch (Exception e){
                    image = "";
                }
                try{
                    ArrayList<Uri> storeImage = new ArrayList<>();
                    File file = new File(getContext().getApplicationContext().getCacheDir(), StringGenerator.getSaltString());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(BankBase64.decode(image , 0));
                    bos.flush();
                    bos.close();
                    Uri uri = Uri.fromFile(file);
                    storeImage.add(uri);
                    array.add(new Store(storeID,storeName,storeImage,storeSalesmanID,storeSlogan,storeDescription,storeOpeningDate,null));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

//    public class NewestStores extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getAllParlors.do");
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
//                    Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    newestStoreList =getStoresFromServer(o.toString());
//                    Log.e("newestStore" , Integer.toString(newestStoreList.size()));
//                    doNew();
//                }
//            } else {
//
//            }
//        }
//    }
//
//    public class PopularStores extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            Log.e("MOHSEN" , "SADE");
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getAllParlors.do");
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
//                    Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    popularStoreList = getStoresFromServer(o.toString());
//                    Log.e("popularStore" , Integer.toString(popularStoreList.size()));
//                    doPopular();
//                }
//            } else {
//
//            }
//        }
//    }
//
//    public class BestStores extends AsyncTask {
//        @Override
//        protected Object doInBackground(Object[] params) {
//            Log.e("enter" , "background");
//            try {
//                Log.e("MOhsen" , "Background");
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getAllParlors.do");
//                Log.e("MOHSEN" , url.toString());
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.connect();
//                //     URLConnection urlConnection = url.openConnection();
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
//                String content = o.toString();
//                if(o.equals("0")){
//                    Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Log.e("MOHSEN" , "ENTRT");
//                    bestStoreList = getStoresFromServer(o.toString());
//                    Log.e("bestStore" , Integer.toString(bestStoreList.size()));
//                    doBest();
//                }
//            } else {
//                Log.e("Yegane" , "yahya");
//            }
//        }
//    }
}
