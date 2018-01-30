package com.example.navid.androidproject.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.DialerKeyListener;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.List;

import com.example.navid.androidproject.Activity.CompleteListActivity;
import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.NoConnectionActivity;
import com.example.navid.androidproject.Activity.ProductActivity;
import com.example.navid.androidproject.Adapter.BanerViewPagerAdapter;
import com.example.navid.androidproject.Adapter.CategoryImageAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Adapter.ProductAdapter;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;

public class HomeFragment extends Fragment{

    private ViewPager banerViewPager;
    private BanerViewPagerAdapter banerViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private ArrayList<Uri> banerImages;

    private RecyclerView bestRecyclerView;
    private RecyclerView popularRecyclerView;
    private RecyclerView newestRecyclerView;

    private ProductAdapter bestAdapter;
    private ProductAdapter popularAdapter;
    private ProductAdapter newestAdapter;

    private ArrayList<Product> bestProductList;
    private ArrayList<Product> popularProductList;
    private ArrayList<Product> newestProductList;

    private TextView homeTitle;

    private SwipeRefreshLayout refresh;

    View myView;

    RequestQueue newQueue;
    RequestQueue bestQueue;
    RequestQueue popularQueue;

    private InternetConnection internetConnection;

    private String title;
    private int page;

//    public static HomeFragment newInstance(int page, String title) {
//        HomeFragment homeFragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        homeFragment.setArguments(args);
//        return homeFragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        page = getArguments().getInt("someInt", 0);
//        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_home, container, false);

        banerImages = new ArrayList<Uri>();

        bestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        newQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        popularQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        internetConnection = new InternetConnection(this.getActivity());

        if(getActivity().getIntent().getStringExtra("beforeHome") != null) {
            getActivity().getIntent().removeExtra("beforeHome");
            newQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixNewestProducts.do",true);
        }
  //      if(StaticInfo.BEST_PRODUCTS == null) {
            getBestData();
//        }else{
//            bestProductList = StaticInfo.BEST_PRODUCTS;
//            doBest();
//        }
//        if(StaticInfo.NEWEST_PRODUCTS == null) {
            getNewData();
//        }else{
//            newestProductList = StaticInfo.NEWEST_PRODUCTS;
//            doNew();
//        }
//        if(StaticInfo.POPULAR_PRODUCTS == null) {
            getPopularData();
//        }else{
//            popularProductList = StaticInfo.POPULAR_PRODUCTS;
//            doPopular();
//        }

        banerViewPager = (ViewPager) myView.findViewById(R.id.baner);
        banerViewPager.setFocusable(true);
        banerViewPager.setCurrentItem(banerImages.size() - 1);
        banerViewPager.setRotationY(180);

        dotsLayout = (LinearLayout) myView.findViewById(R.id.banerDots);
        addBottomDots(banerImages.size()- 1);

        // making notification bar transparent
        changeStatusBarColor();

        banerViewPagerAdapter = new BanerViewPagerAdapter(getContext() , banerImages);
        banerViewPager.setAdapter(banerViewPagerAdapter);
        banerViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        banerViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TO DO
                return false;
            }
        });

        TextView text = (TextView) myView.findViewById(R.id.comList1);
        TextView text2 = (TextView) myView.findViewById(R.id.comList2);
        TextView text3 = (TextView) myView.findViewById(R.id. comList3);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()){
                    Intent intent = new Intent(getActivity().getApplicationContext() , CompleteListActivity.class);
                    StaticInfo.SAVED_COMPLETE_LIST = "بهترین‌ها (قیمت)";
                    intent.putExtra("completeListName" , "Tops");
                    getActivity().finish();
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getContext(), NoConnectionActivity.class);
                    StaticInfo.SAVED_COMPLETE_LIST = "بهترین‌ها (قیمت)";
                    intent.putExtra("completeListName" , "Tops");
                    intent.putExtra("activity" , 10);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()){
                    Intent intent = new Intent(getActivity().getApplicationContext() , CompleteListActivity.class);
                    StaticInfo.SAVED_COMPLETE_LIST = "بهترین‌ها (کیفیت)";
                    intent.putExtra("completeListName" , "Favs");
                    getActivity().finish();
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getContext(), NoConnectionActivity.class);
                    StaticInfo.SAVED_COMPLETE_LIST = "بهترین‌ها (کیفیت)";
                    intent.putExtra("completeListName" , "Favs");
                    intent.putExtra("activity" , 10);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()){
                    Intent intent = new Intent(getActivity().getApplicationContext() , CompleteListActivity.class);
                    StaticInfo.SAVED_COMPLETE_LIST = "جدیدترین‌ها";
                    intent.putExtra("completeListName" , "Newest");
                    getActivity().finish();
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getContext(), NoConnectionActivity.class);
                    StaticInfo.SAVED_COMPLETE_LIST = "جدیدترین‌ها";
                    intent.putExtra("completeListName" , "Newest");
                    intent.putExtra("activity" , 10);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        SpannableString ss = new SpannableString("لیست کامل");
        ss.setSpan(new UnderlineSpan(), 0 , ss.length() , 0);
        text.setText(ss);
        text2.setText(ss);
        text3.setText(ss);

        refresh = (SwipeRefreshLayout) myView.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixNewestProducts.do",true);
                bestQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixTopsProducts.do",true);
                popularQueue.getCache().invalidate("http://" + Server.IP + ":" + Server.PORT + "/getSixFavsProducts.do",true);
//                StaticInfo.BEST_PRODUCTS = null;
//                StaticInfo.NEWEST_PRODUCTS = null;
//                StaticInfo.POPULAR_PRODUCTS = null;
                getNewData();
                getBestData();
                getPopularData();
                refresh.setRefreshing(false);
            }
        });

        return myView;
    }

    public void doBest(){
		Log.e("Realmadrid","acmilan");
        bestRecyclerView = (RecyclerView) myView.findViewById(R.id.bestSellingRecyclerView);
        bestAdapter = new ProductAdapter(myView.getContext() , bestProductList , this);
        bestRecyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext() , LinearLayoutManager.HORIZONTAL ,true));
        bestRecyclerView.setAdapter(bestAdapter);
    }

    public void doPopular(){
        popularRecyclerView = (RecyclerView) myView.findViewById(R.id.mostPopularRecyclerView);
        popularAdapter = new ProductAdapter(myView.getContext() , popularProductList , this);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext() , LinearLayoutManager.HORIZONTAL , true));
        popularRecyclerView.setAdapter(popularAdapter);
    }

    public void doNew(){
        newestRecyclerView = (RecyclerView) myView.findViewById(R.id.newestRecyclerView);
        newestAdapter = new ProductAdapter(myView.getContext() , newestProductList , this);
        newestRecyclerView.setLayoutManager(new LinearLayoutManager(myView.getContext() , LinearLayoutManager.HORIZONTAL , true));
        newestRecyclerView.setAdapter(newestAdapter);
    }

    private void getPopularData(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixFavsProducts.do";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    popularProductList = getProductsFromServer(response);
                    //StaticInfo.POPULAR_PRODUCTS = popularProductList;
                    doPopular();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
//                Toast.makeText(getContext().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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

    private void getBestData(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixTopsProducts.do";

        Log.e("Barcelona" , url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    bestProductList = getProductsFromServer(response);
                    //StaticInfo.BEST_PRODUCTS = bestProductList;
                    doBest();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            //    Toast.makeText(getContext().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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

    private void getNewData() {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getSixNewestProducts.do";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    newestProductList = getProductsFromServer(response);
                    //StaticInfo.NEWEST_PRODUCTS = newestProductList;
                    doNew();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
         //       Toast.makeText(getContext().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
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

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /*
        after click to cardView open new fragment : Product fragment
     */
    public void openNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("pre" , 0);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getContext(), NoConnectionActivity.class);
            intent.putExtra("pre" , 0);
            intent.putExtra("activity" , 2);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }
    public ArrayList<Uri> getImageUri(String[] images){

        ArrayList<Uri> imagesUri = new ArrayList<>();
        for(int i = 0 ; i < images.length ;i++){
            if(images[i] != ""){
                try{
                    File file = new File(getContext().getApplicationContext().getCacheDir(), StringGenerator.getSaltString());
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

            Log.e("decodecontent" , decodeContent);

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
                array.add(new Product(productID,productName,productPrice,productOffPrice,productStoreID,productStore,getImageUri(images),productQuantity,productDescription,productSubCategory));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

//    public class NewestProducts extends AsyncTask {
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
//                    Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    newestProductList = getProductsFromServer(o.toString());
//                    doNew();
//                }
//            } else {
//
//            }
//        }
//    }
//
//    public class PopularProducts extends AsyncTask {
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
//                    Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    popularProductList = getProductsFromServer(o.toString());
//                    doPopular();
//                }
//            } else {
//
//            }
//        }
//    }
//
//    public class BestProducts extends AsyncTask {
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
//                    Toast.makeText(getActivity().getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Log.e("CONTENT" , content);
//                    bestProductList = new ArrayList<>();
//                    bestProductList = getProductsFromServer(content);
//                    doBest();
//                    Log.e("bestProducts" , Integer.toString(bestProductList.size()));
//                }
//            } else {
//                Log.e("BYE", "Heidar");
//            }
//        }
//    }
}
