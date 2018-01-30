package com.example.navid.androidproject.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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

import org.apache.commons.io.IOUtils;
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
import com.example.navid.androidproject.Fragment.MyDialogFragment;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.TemporaryStoreProductsList;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StoreActivity extends AppCompatActivity {

    private TextView storeName;
    private ImageView _backArrow;
    private ImageView _share;
    private ImageView _favorite;
    private ImageView _storeImage;
    private ImageView _info;
    private TextView _slogan;
    private TextView _report;
    private TextView _storeReview;
    private TextView _storeDescprition;

    private RatingBar qualityRatingBar;
    private RatingBar priceRatingBar;

    private Float priceRateFloat = 0.0f;
    private Float qualityRateFloat = 0.0f;

    private ArrayList<Uri> banerImages;
    private ArrayList<Product> products;

    private ProductAdapterGridLayout productAdapterGridLayout;
    private RecyclerView productRecyclerView;

    public String _storeName;
    public ArrayList<Uri> _storeImages;

    RequestQueue requestQueue,sendQueue,detailQueue;

    public SQLiteDatabase favoriteStore;

    MyDialogFragment myDialogFragment;

    private InternetConnection internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        _slogan = (TextView) findViewById(R.id.bannertext);
        _storeImage = (ImageView) findViewById(R.id.ivId);
        storeName = (TextView) findViewById(R.id.storeName);
        _storeDescprition = (TextView) findViewById(R.id.descriptionText);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sendQueue = Volley.newRequestQueue(getApplicationContext());
        detailQueue = Volley.newRequestQueue(getApplicationContext());

        if(getIntent().getIntExtra("pre" , -1) == 4){   //go from productActivity
            getDetailsData();
        }
        else{
            getData(StaticInfo.SAVED_STORE.getID());

            _storeName = StaticInfo.SAVED_STORE.getName();
            storeName.setText(_storeName);
            _slogan.setText(StaticInfo.SAVED_STORE.getSlogan());
            _storeImages = StaticInfo.SAVED_STORE.getImage();
            _storeDescprition.setText(StaticInfo.SAVED_STORE.getDescription().replace('_',' '));
            banerImages = StaticInfo.SAVED_STORE.getImage();

            if(!_storeImages.isEmpty()) {
                _storeImage.setImageURI(_storeImages.get(0));
            }
            else{
                _storeImage.setImageURI(null);
            }
        }

//        GetQualityRate getQualityRate = new GetQualityRate();
//        getQualityRate.execute();
//
//        GetPriceRate getPriceRate = new GetPriceRate();
//        getPriceRate.execute();

        internetConnection = new InternetConnection(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        myDialogFragment = MyDialogFragment.newInctance("StoreActivity","با سلام" , "بستن" , R.drawable.waiter);
        myDialogFragment.show(getFragmentManager(),"dialog");

        qualityRatingBar = (RatingBar) findViewById(R.id.qualityRatingBar);
        qualityRatingBar.setRating(qualityRateFloat);

        priceRatingBar = (RatingBar) findViewById(R.id.priceRatingBar);
        priceRatingBar.setRating(priceRateFloat);

        _backArrow = (ImageView) findViewById(R.id.backFromStore);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pre = getIntent().getIntExtra("pre" , -1);
                Intent intent;
                switch (pre) {
                    case 1:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("fragment","store");
                        break;
                    case 2:
                        intent = new Intent(getApplicationContext(), StoreCompleteListActivity.class);
                        intent.putExtra("completeListName" , getIntent().getStringExtra("completeListName"));
                        break;
                    case 3:
                        intent = new Intent(getApplicationContext(), InterestsActivity.class);
                        break;
                    case 4:
                        intent = new Intent(getApplicationContext(), ProductActivity.class);
                        intent.putExtra("pre" , getIntent().getIntExtra("prepre" , -1));
                        break;
                    default:
                        intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("fragment","store");
                        break;
                }
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    });

        _report = (TextView) findViewById(R.id.reportTV);
        _report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                final String[] items = {"محتوای نامناسب", "اطلاعات تماس نادرست است", "اطلاعات محصولات گمراه‌کننده و یا دروغ است", "فروشگاه در دسته‌بندی نامرتبط قرار گرفته‌است", "قیمت محصولات نامناست است" , "دلایل دیگر ..."};
                AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setTitle("لطفا گزینه مورد نظر خود را انتخاب کنید");
                builder.setSingleChoiceItems(items,0,null);
                builder.setPositiveButton("تائید" , new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        // Do something useful withe the position of the selected radio button
                    }
                });
                dialog = builder.create();
                dialog.getWindow().setGravity(Gravity.RIGHT);
                dialog.show();
            }
        });

        _share = (ImageView) findViewById(R.id.share);
        _share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://google.com");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent , "پیشنهاد به دوستان"));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _favorite = (ImageView) findViewById(R.id.favorite);
        _favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteStore = openOrCreateDatabase("favoriteStore" , MODE_PRIVATE , null);
                favoriteStore.beginTransaction();
                Cursor cursor = favoriteStore.rawQuery("SELECT * FROM favoriteStore",null);
                boolean result = true;
                while (cursor.moveToNext()){
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    if(name.equals(storeName.getText().toString())) {
                        result = false;
                    }
                }
                if(result){
                    try {
                        sendData();
                        String imageString = StaticInfo.SAVED_STORE.getImage().get(0).getPath();
                        favoriteStore.execSQL("INSERT INTO favoriteStore (id,name,images,salesmanID,slogan,description,openingDate,products) VALUES (?,?,?,?,?,?,?,?)",
                                new String[]{StaticInfo.SAVED_STORE.getID(), StaticInfo.SAVED_STORE.getName(),imageString , StaticInfo.SAVED_STORE.getSalesmanID(), StaticInfo.SAVED_STORE.getSlogan(), StaticInfo.SAVED_STORE.getDescription(), StaticInfo.SAVED_STORE.getOpeningDate(), StringArrayList.convertProductArrayListToString(StaticInfo.SAVED_STORE.getProduct())});
                        Toast.makeText(getApplicationContext(), "به علاقه‌‌مندی‌های شما اضافه شد", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext() ,"این حجره در علاقه‌مندی‌های شما موجود است" , Toast.LENGTH_SHORT).show();
                }
                favoriteStore.setTransactionSuccessful();
                favoriteStore.endTransaction();
                favoriteStore.close();
            }
        });

        _info = (ImageView) findViewById(R.id.infoss);
        _info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myDialogFragment = MyDialogFragment.newInctance("StoreActivity","با سلام" , "بستن" , R.drawable.waiter);
                myDialogFragment.show(getFragmentManager(),"dialog");
            }
        });

//        _storeReview = (TextView) findViewById(R.id.storeReviewButton);
//        _storeReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(getApplicationContext() , StoreReviewActivity.class);
////                //intent.putExtra("prepre" , 2);
////                startActivityForResult(intent,1);
////                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });
    }

    public void sendData(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/likedPlus.do?parlorID=" + StaticInfo.SAVED_STORE.getID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    if(response.equals("1")){

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"دوباره تلاش کنید",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
        sendQueue.add(stringRequest);
    }

    private void getData(String storeID) {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getProductsOfParlor.do?" + "parlorID=" + storeID;

        Log.e("getData", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    products = getProductsFromServer(response);
                    doAfter();
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

    private void getDetailsData() {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/parlorDetails.do?" + "parlorID=" + StaticInfo.SAVED_PRODUCT.getStoreID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    getStoreDetailFromServer(response);
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
        detailQueue.add(stringRequest);
    }

    public void doAfter(){
        productRecyclerView = (RecyclerView) findViewById(R.id.productRecyclerView);
        RecyclerView.LayoutManager prodcutLayoutManager =  new GridLayoutManager(this , 2);
        productRecyclerView.setLayoutManager(prodcutLayoutManager);
        productAdapterGridLayout = new ProductAdapterGridLayout(getApplicationContext() , products , this);
        productRecyclerView.setAdapter(productAdapterGridLayout);
    }

    public void openNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra("pre" , 1);
            intent.putExtra("prepre" , getIntent().getIntExtra("pre" , -1));
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            intent.putExtra("pre" , 1);
            intent.putExtra("prepre" , getIntent().getIntExtra("pre" , -1));
            intent.putExtra("activity" , 2);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        int pre = getIntent().getIntExtra("pre" , -1);
        Intent intent;
        switch (pre) {
            case 1:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fragment","store");
                break;
            case 2:
                intent = new Intent(getApplicationContext(), StoreCompleteListActivity.class);
                intent.putExtra("completeListName" , getIntent().getStringExtra("completeListName"));
                break;
            case 3:
                intent = new Intent(getApplicationContext(), InterestsActivity.class);
                break;
            case 4:
                intent = new Intent(getApplicationContext(), ProductActivity.class);
                intent.putExtra("pre" , getIntent().getIntExtra("prepre" , -1));
                break;
            default:
                intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("fragment","store");
                break;
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void doPositiveClick(){
        myDialogFragment.dismiss();
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

    public void getStoreDetailFromServer(String input){
        try{
            JSONParser jsonParser = new JSONParser();
            String decodeContent = "";
            AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123" , "arsham");
            AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(input);
            decodeContent = AesCbcWithIntegrity.decryptString(value,key);

            JSONArray jsonArray = (JSONArray) jsonParser.parse(decodeContent);

            for(int i = 0 ; i < jsonArray.size() ; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String storeID = jsonObject.get("id").toString();
                String Name = jsonObject.get("name").toString();
                String storeSlogan = jsonObject.get("slogan").toString();
                storeSlogan = storeSlogan.replace('_',' ');
                String storeSalesmanID = jsonObject.get("salesmanID").toString();
                String storeOpeningDate =jsonObject.get("openingDate").toString();
                String storeDescription = jsonObject.get("description").toString().replace('_',' ');
                String image;
                try{
                    image = jsonObject.get("poster").toString();
                }catch (Exception e){
                    image = "";
                }
                try{
                    File file = new File(getCacheDir(),StringGenerator.getSaltString());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bos.write(BankBase64.decode(image , 0));
                    bos.flush();
                    bos.close();
                    Uri uri = Uri.fromFile(file);
                    _storeImage.setImageURI(uri); //replace with uri

                    _storeName = Name;
                    storeName.setText(_storeName);
                    _storeDescprition.setText(storeDescription);
                    _slogan.setText(storeSlogan);

                    getData(storeID);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Product> getProductsFromServer(String input){
        ArrayList<Product> output = new ArrayList<>();
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
                String productStore = jsonObject.get("parlorName").toString();
                String productStoreID = jsonObject.get("parlorID").toString();
                String productSubCategory = jsonObject.get("subcategoryName").toString();
                String productQuantity =jsonObject.get("quantity").toString();
                String productDescription = jsonObject.get("description").toString();
                String[] images = new String[5];
                try {
                    images[0] = jsonObject.get("image1").toString();
                    Log.e("mamadali_1" , images[0]);
                }
                catch (Exception e){
                    images[0] = "";
                }
                try {
                    images[1] = jsonObject.get("image2").toString();
                    Log.e("mamadali_2" , images[1]);
                }
                catch (Exception e){
                    images[1] = "";
                }
                try {
                    images[2] = jsonObject.get("image3").toString();
                    Log.e("mamadali_3" , images[2]);
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
                output.add(new Product(productID,productName,productPrice,productOffPrice,productStoreID,productStore,getImageUri(images),productQuantity,productDescription,productSubCategory));
            }
            Log.e("productList" , Integer.toString(output.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

//    public class GetProducts extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getProductsOfParlor.do?" +
//                        "parlorID="+ params[0].toString());
//
//                Log.e("mohsen" , url.toString());
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
//
//                }
//                else{
//                    getProductsFromServer(content);
//                    doAfter();
//                }
//            } else {
//
//            }
//        }
//    }

//    public class StoreDetail extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/parlorDetails.do?" +
//                        "parlorID=" + StaticInfo.SAVED_PRODUCT.getStoreID());
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
//                    Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    getStoreDetailFromServer(o.toString());
//                }
//            } else {
//
//            }
//        }
//    }

    public class GetPriceRate extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getParlorES.do?" +
                        "parlorID="+StaticInfo.SAVED_STORE.getID());

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
                priceRateFloat = Float.parseFloat(content);
            } else {
                Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
                priceRateFloat = 0.0f;
            }
        }
    }

    public class GetQualityRate extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getParlorQS.do?" +
                        "parlorID="+StaticInfo.SAVED_STORE.getID());

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
                qualityRateFloat = Float.parseFloat(content);
            } else {
                Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
                qualityRateFloat = 0.0f;
            }
        }
    }
}
