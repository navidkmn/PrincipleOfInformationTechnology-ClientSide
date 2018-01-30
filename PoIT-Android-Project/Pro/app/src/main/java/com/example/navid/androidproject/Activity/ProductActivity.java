package com.example.navid.androidproject.Activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navid.androidproject.Adapter.ProductAdapterGridLayout;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Other.CartList;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.ShoppingBagProduct;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
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
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.example.navid.androidproject.Adapter.BanerViewPagerAdapter;
import com.example.navid.androidproject.Other.InternetConnection;

public class ProductActivity extends AppCompatActivity {

    public static int GO_TO_ZOOM_ACTIVITY = 0;

    private ImageView _backArrow;
    private ImageView _card;
    private ImageView _favorite;
    private ImageView _share;

    private TextView productName;
    private TextView productPrice;
    private TextView productStore;
    private TextView productQuantity;
    private TextView productSubcategory;
    private RatingBar priceRate;
    private RatingBar qualityRate;
    private TextView productDescription;
    private TextView _productReview;

    private Float priceRateFloat = 0.0f;
    private Float qualityRateFloat = 0.0f;

    private ViewPager banerViewPager;
    private BanerViewPagerAdapter banerViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private ArrayList<Uri> banerImages;

    private RequestQueue requestQueue,priceQueue,qualityQueue,sendQueue;

    private SQLiteDatabase favoriteProduct;

    int pre;

    float oldX = 0,newX = 0,oldY = 0,newY = 0;

    private InternetConnection internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        internetConnection = new InternetConnection(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        productName = (TextView) findViewById(R.id.productName);
        productPrice = (TextView) findViewById(R.id.productPrice);
        productStore = (TextView) findViewById(R.id.productStore);
        productSubcategory = (TextView) findViewById(R.id.productSubCategory);
        priceRate = (RatingBar) findViewById(R.id.priceRatingBar);
        qualityRate = (RatingBar) findViewById(R.id.qualityRatingBar);
        productDescription = (TextView) findViewById(R.id.description);
        productQuantity = (TextView) findViewById(R.id.productQuantity);
        productQuantity.setText(StaticInfo.SAVED_PRODUCT.getQuantity());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        priceQueue = Volley.newRequestQueue(getApplicationContext());
        qualityQueue = Volley.newRequestQueue(getApplicationContext());
        sendQueue = Volley.newRequestQueue(getApplicationContext());

        if(StaticInfo.SAVED_PRODUCT.getImage().size() == 1)
            getData();

        getPriceRate();
        getQualityRate();

//        GetPriceRate getPriceRate = new GetPriceRate();
//        getPriceRate.execute();
//
//        GetQualityRate getQualityRate = new GetQualityRate();
//        getQualityRate.execute();

        banerImages = StaticInfo.SAVED_PRODUCT.getImage();
        doAfter();

        productName.setText(StaticInfo.SAVED_PRODUCT.getName());
        productPrice.setText(StaticInfo.SAVED_PRODUCT.getPrice());
        productSubcategory.setText(StaticInfo.SAVED_PRODUCT.getSubCategoryName());
        productStore.setText(StaticInfo.SAVED_PRODUCT.getStoreName());
        productStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                    intent.putExtra("pre", 4);
                    intent.putExtra("prepre" ,pre);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                    intent.putExtra("pre", 4);
                    intent.putExtra("prepre" ,pre);
                    intent.putExtra("activity", 4);
                    startActivity(intent);
                    finish();
                }
            }
        });

        productDescription.setText(StaticInfo.SAVED_PRODUCT.getDescription().replace('_',' '));

        _backArrow = (ImageView) findViewById(R.id.backFromProduct);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(internetConnection.haveNetworkConnection()) {

                    Intent intent;
                    switch (pre) {
                        case 0:
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            break;
                        case 1:
                            intent = new Intent(getApplicationContext(), StoreActivity.class);
                            intent.putExtra("pre" , getIntent().getIntExtra("prepre" , -1));
                            startActivity(intent);
                            finish();
                            break;
                        case 2:
                            intent = new Intent(getApplicationContext(), SubSubCategoryActivity.class);
                            intent.putExtra("subCategoryName", getIntent().getStringExtra("subCategoryName"));
                            startActivity(intent);
                            finish();
                            break;
                        case 3:
                            intent = new Intent(getApplicationContext(), CompleteListActivity.class);
                          //  intent.putExtra("completeListName", getIntent().getStringExtra("completeListName"));
                            startActivity(intent);
                            finish();
                            break;
                        case 4:
                            intent = new Intent(getApplicationContext(), InterestsActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case 5:
                            intent = new Intent(getApplicationContext(), SearchActivity.class);
                            intent.putExtra("pre" , 0);
                            startActivity(intent);
                            finish();
                            break;
                        default:
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            break;
                    }
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _card = (ImageView) findViewById(R.id.card);
        _card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase cart = openOrCreateDatabase("cart" , MODE_PRIVATE , null);
                cart.beginTransaction();
                Cursor cursor = cart.rawQuery("SELECT * FROM cartProducts",null);
                boolean result = true;

                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    if(id.equals(StaticInfo.SAVED_PRODUCT.getID())) {
                        result = false;
                    }
                }
                if(result) {
                        try {
//                            InputStream inputStream = getContentResolver().openInputStream(banerImages.get(0));
//                            String imageString = BankBase64.encodeToString(IOUtils.toByteArray(inputStream), 0);
                            String imageString = StaticInfo.SAVED_PRODUCT.getImage().get(0).getPath();
                            cart.execSQL("INSERT INTO cartProducts (id,name, price, image, storeName, quantity) VALUES (?,?,?,?,?,?)",
                                    new String[]{StaticInfo.SAVED_PRODUCT.getID(),StaticInfo.SAVED_PRODUCT.getName(), StaticInfo.SAVED_PRODUCT.getPrice(), imageString, StaticInfo.SAVED_PRODUCT.getStoreName(), "1"});
                            Toast.makeText(getApplicationContext(), "به سبد خرید شما اضافه شد", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else{
                        Toast.makeText(getApplicationContext(), "این کالا در سبد خرید شما موجود است", Toast.LENGTH_SHORT).show();
                    }
                    cart.setTransactionSuccessful();
                    cart.endTransaction();
                    cart.close();
                }
        });

        _favorite = (ImageView) findViewById(R.id.favorite);
        _favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteProduct = openOrCreateDatabase("favoriteProduct" , MODE_PRIVATE , null);
                favoriteProduct.beginTransaction();
                Cursor cursor = favoriteProduct.rawQuery("SELECT * FROM favoriteProduct",null);
                boolean result = true;
                while (cursor.moveToNext()){
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String storeName = cursor.getString(cursor.getColumnIndex("storeName"));
                    if(name.equals(productName.getText().toString()) && storeName.equals(productStore.getText().toString())) {
                        result = false;
                    }
                }
                if(result){
                    String[] images = new String[5];
                    for(int i = 0 ; i < StaticInfo.SAVED_PRODUCT.getImage().size() ; i++)
                        images[i] = StaticInfo.SAVED_PRODUCT.getImage().get(i).getPath();
                    for(int i = StaticInfo.SAVED_PRODUCT.getImage().size() ; i < 5 ; i++)
                        images[i] = "null";

                    favoriteProduct.execSQL("INSERT INTO favoriteProduct (id,name,price,offPrice,image1,image2,image3,image4,image5,storeName,storeID,subCategory,quantity,description) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            new String[]{StaticInfo.SAVED_PRODUCT.getID(),StaticInfo.SAVED_PRODUCT.getName(),StaticInfo.SAVED_PRODUCT.getPrice(),StaticInfo.SAVED_PRODUCT.getOffPrice(),images[0],images[1],images[2],images[3] , images[4],StaticInfo.SAVED_PRODUCT.getStoreName(),StaticInfo.SAVED_PRODUCT.getStoreID(),StaticInfo.SAVED_PRODUCT.getSubCategoryName(),StaticInfo.SAVED_PRODUCT.getQuantity(),StaticInfo.SAVED_PRODUCT.getDescription()});
                    Toast.makeText(getApplicationContext() ,"به علاقه‌‌مندی‌های شما اضافه شد" , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext() ,"این کالا در علاقه‌مندی‌های شما موجود است" , Toast.LENGTH_SHORT).show();
                }

                favoriteProduct.setTransactionSuccessful();
                favoriteProduct.endTransaction();
                favoriteProduct.close();
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
            }
        });

        _productReview = (TextView) findViewById(R.id.productReviewButton);
        _productReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()){
                    Intent intent = new Intent(getApplicationContext() , ProductReviewActivity.class);
                    startActivityForResult(intent,1);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity", 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        switch (pre){
            case 0 :
                startActivity(new Intent(getApplicationContext() , MainActivity.class));
                finish();
                break;
            case 1 :
                intent = new Intent(getApplicationContext() , StoreActivity.class);
//                intent.putExtra("storeName", getIntent().getStringExtra("storeName"));
//                intent.putExtra("storeImages" , getIntent().getIntegerArrayListExtra("storeImages"));
                intent.putExtra("pre" , getIntent().getIntExtra("prepre" , -1));
                startActivity(intent);
                finish();
                break;
            case 2:
                intent = new Intent(getApplicationContext() , SubSubCategoryActivity.class);
                intent.putExtra("subCategoryName" , getIntent().getStringExtra("subCategoryName"));
                startActivity(intent);
                finish();
                break;
            case 3:
                intent = new Intent(getApplicationContext(), CompleteListActivity.class);
             //   intent.putExtra("completeListName", getIntent().getStringExtra("completeListName"));
                startActivity(intent);
                finish();
                break;
            case 4:
                intent = new Intent(getApplicationContext(), InterestsActivity.class);
                startActivity(intent);
                finish();
                break;
            case 5:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[banerImages.size()];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0 ; i < dots.length ; i++) {
            dots[i] = new TextView(getApplicationContext());
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

    private void doAfter(){
        banerViewPager = (ViewPager) findViewById(R.id.baner);
        dotsLayout = (LinearLayout) findViewById(R.id.banerDots);

        banerViewPager.setCurrentItem(banerImages.size() - 1);
        banerViewPager.setRotationY(180);

        addBottomDots(banerImages.size()- 1);

        pre = getIntent().getIntExtra("pre" , -1);

        banerViewPagerAdapter = new BanerViewPagerAdapter(getApplicationContext() , banerImages);
        banerViewPager.setAdapter(banerViewPagerAdapter);
        banerViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        banerViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        oldY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        newX = event.getX();
                        newY = event.getY();
                        Log.e("newX" , Float.toString(newX));
                        Log.e("oldX" , Float.toString(oldX));
                        if(Math.abs(newX - oldX) < 10 && Math.abs(newY - oldY) < 10){
                            Intent intent = new Intent(getApplicationContext(),ZoomActivity.class);
                            intent.putExtra("images" , banerImages);
                            startActivityForResult(intent , 0);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private ArrayList<Uri> getImages(String input){
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
                return getImageUri(images);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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


    private void getData() {
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getAllPicsOfProduct.do?productID=" + StaticInfo.SAVED_PRODUCT.getID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    if(response.equals("0")){
                        Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        banerImages = getImages(response);
                        StaticInfo.SAVED_PRODUCT.setImage(banerImages);
                        doAfter();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getPriceRate(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getESofProduct.do?" + "productID="+StaticInfo.SAVED_PRODUCT.getID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    priceRateFloat = Float.parseFloat(response);
                    priceRate.setRating(priceRateFloat);
                }
                else{
                    Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
                    priceRateFloat = 0.0f;
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
        priceQueue.add(stringRequest);
    }

    public void getQualityRate(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/getQSofProduct.do?" + "productID="+StaticInfo.SAVED_PRODUCT.getID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    qualityRateFloat = Float.parseFloat(response);
                    qualityRate.setRating(qualityRateFloat);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
                        qualityRateFloat = 0.0f;
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
        qualityQueue.add(stringRequest);
    }

//    public class GetPriceRate extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getESofProduct.do?" +
//                        "productID="+StaticInfo.SAVED_PRODUCT.getID());
//
//                Log.e("Alireza",url.toString());
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
//                priceRateFloat = Float.parseFloat(content);
//            } else {
//                Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
//                priceRateFloat = 0.0f;
//            }
//        }
//    }

//    public class GetQualityRate extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getQSofProduct.do?" +
//                        "productID="+StaticInfo.SAVED_PRODUCT.getID());
//
//                Log.e("Alireza",url.toString());
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
//                qualityRateFloat = Float.parseFloat(content);
//            } else {
//                Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
//                qualityRateFloat = 0.0f;
//            }
//        }
//    }
}
