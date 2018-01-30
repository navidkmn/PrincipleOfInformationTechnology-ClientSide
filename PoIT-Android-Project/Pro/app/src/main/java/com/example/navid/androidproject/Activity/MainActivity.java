package com.example.navid.androidproject.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navid.androidproject.R;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Locale;

import butterknife.ButterKnife;
import com.example.navid.androidproject.Adapter.TabViewPagerAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AESCrypt;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankAES;
import com.example.navid.androidproject.AndroidEncoderUtil.JavaEncryption;
import com.example.navid.androidproject.Fragment.CategoryFragment;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Fragment.MyDialogFragment;
import com.example.navid.androidproject.Fragment.StoreFragment;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.MyCalendar;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_AppMain = 0;
    private static final int TIME_INTERVAL = 3000;
    private long mBackPressed;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navigationHeader;
    private TextView navigationHeaderText;
    private TextView navigationVersionText;

    private RequestQueue requestQueue;

    private TabLayout tabLayout;
    private ViewPager tabViewPager;

    private ImageView _menuBar;
    private ImageView _search;
    private ImageView _bascket;
    private ImageView _interests;

    public SQLiteDatabase database;
    public SQLiteDatabase cart;
    public SQLiteDatabase favoriteProduct;
    public SQLiteDatabase favoriteStore;

    MyDialogFragment myDialogFragment;

    InternetConnection internetConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String version = "";
        PackageManager manager = getApplicationContext().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = info.versionName;
        }catch (Exception e){
            e.printStackTrace();
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        internetConnection = new InternetConnection(this);

        //////////////// SQLite for saving login
        database = openOrCreateDatabase("Charsoo",MODE_PRIVATE,null);
        database.beginTransaction();
        database.execSQL("CREATE TABLE IF NOT EXISTS Users(customerID nvarchar(10),phoneNumber nvarchar(12) , lastName nvarchar(30) , firstName nvarchar(30) , mailAddress nvarchar(50) , gender char(1) , salesmanOrNot char(1) , storeID nvarchar(10))");
        Cursor cursor = database.rawQuery("SELECT * FROM Users",null);

        /////////////// SQLite for saving cart
        cart = openOrCreateDatabase("cart" , MODE_PRIVATE , null);
        cart.beginTransaction();
        cart.execSQL("CREATE TABLE IF NOT EXISTS cartProducts(id nvarchar(100),name nvarchar(100) , price nvarchar(100), image nvarchar(100), storeName nvarchar(100) , quantity nvarchar(5))");
        cart.setTransactionSuccessful();
        cart.endTransaction();
        cart.close();
        ////////////////

        /////////////// SQLite for saving cart
        favoriteProduct = openOrCreateDatabase("favoriteProduct" , MODE_PRIVATE , null);
        favoriteProduct.beginTransaction();
        favoriteProduct.execSQL("CREATE TABLE IF NOT EXISTS favoriteProduct(id nvarchar(20),name nvarchar(100) , price nvarchar(100), offPrice nvarchar(100),image1 nvarchar(100), image2 nvarchar(100), image3 nvarchar(100),image4 nvarchar(100), image5 nvarchar(100),storeName nvarchar(100) ,storeID nvarchar(100),subCategory nvarchar(100) , quantity nvarchar(20) , description nvarchar(1000))");
        favoriteProduct.setTransactionSuccessful();
        favoriteProduct.endTransaction();
        favoriteProduct.close();
        ////////////////

        /////////////// SQLite for saving cart
        favoriteStore = openOrCreateDatabase("favoriteStore" , MODE_PRIVATE , null);
        favoriteStore.beginTransaction();
        favoriteStore.execSQL("CREATE TABLE IF NOT EXISTS favoriteStore(id nvarchar(20), name nvarchar(100) , images nvarchar(100), salesmanID nvarchar(10) , slogan nvarchar(100) , description nvarchar(1000) , openingDate nvarchar (100),products nvarchar(10000))");
        favoriteStore.setTransactionSuccessful();
        favoriteStore.endTransaction();
        favoriteStore.close();
        ////////////////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        ////////////////////////

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ///////////////////////////
        _menuBar = (ImageView) findViewById(R.id.menuBar);
        _menuBar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        ////////////////////////////
        _search = (ImageView) findViewById(R.id.search);
        _search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivityForResult(intent, REQUEST_AppMain);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity" , 3);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        _bascket = (ImageView) findViewById(R.id.bascket);
        _bascket.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), CardActivity.class);
                    startActivityForResult(intent, REQUEST_AppMain);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity" , 5);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        /////////////////////////
        _interests = (ImageView) findViewById(R.id.interests);
        _interests.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), InterestsActivity.class);
                    startActivityForResult(intent, REQUEST_AppMain);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity" , 1);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        ///////////////////////
        tabViewPager = (ViewPager) findViewById(R.id.tabViewPager);
        setupTabViewPager(tabViewPager);

//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(tabViewPager);
        ////////////////////////
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/A MehdiHeydari.ttf");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationHeader = navigationView.getHeaderView(0);

        navigationVersionText = (TextView) findViewById(R.id.version);
        navigationVersionText.setTypeface(font);
        navigationVersionText.setText("نسخه : "+version);

        navigationHeaderText = (TextView) navigationHeader.findViewById(R.id.signUp_register);
        navigationHeaderText.setTypeface(font);

        if (cursor.moveToNext()){
            User.FIRST_NAME = cursor.getString(cursor.getColumnIndex("firstName"));
            User.LAST_NAME = cursor.getString(cursor.getColumnIndex("lastName"));
            User.MAIL_ADDRESS = cursor.getString(cursor.getColumnIndex("mailAddress"));
            User.GENDER = cursor.getString(cursor.getColumnIndex("gender"));
            User.PHONE_NUMBER = cursor.getString(cursor.getColumnIndex("phoneNumber"));
            User.SALESMAN_OR_NOT = cursor.getString(cursor.getColumnIndex("salesmanOrNot"));
            User.CUSTOMER_ID = cursor.getString(cursor.getColumnIndex("customerID"));
            User.STORE_ID = cursor.getString(cursor.getColumnIndex("storeID"));
            User.LOGIN_STATUS = true;

            navigationHeaderText.setText(User.FIRST_NAME + " " + User.LAST_NAME);
        }
        else{
            navigationHeaderText.setText("ورود / ثبت نام");
            User.LOGIN_STATUS = false;
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();

        navigationHeaderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!User.LOGIN_STATUS) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    drawerLayout.closeDrawers();
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        setupNavigationView();
    } // end onCreate

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(drawerLayout.isDrawerOpen(navigationView))
            drawerLayout.closeDrawers();
        else{
            if(tabViewPager.getCurrentItem() == 1){
                if(mBackPressed + TIME_INTERVAL > System.currentTimeMillis()){
                    super.onBackPressed();
                    return;
                }
                else{
                    Toast.makeText(getApplicationContext() ,"برای خروج بار دیگر دکمه بازگشت را فشار دهید" , Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();
                }
            }
            else
                tabViewPager.setCurrentItem(1);
        }
    }

    private void setupTabViewPager (ViewPager viewPager){
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFrag(new StoreFragment() , "حجره‌ها");
//        adapter.addFrag(new CategoryFragment() , "دسته‌بندی");
//        adapter.addFrag(new HomeFragment() , "خانه");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        String fragment = getIntent().getStringExtra("fragment");
        if(fragment != null) {
            switch (fragment){
                case "category":
                    viewPager.setCurrentItem(0);
                    break;
                case "store":
                    viewPager.setCurrentItem(2);
                    break;
                default:
                    viewPager.setCurrentItem(adapter.getCount() - 2);
                    break;
            }
        }
        else
            viewPager.setCurrentItem(adapter.getCount() - 2);
    }

    private void setupNavigationView(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.updateAccount :
                        if(!User.LOGIN_STATUS){
                            myDialogFragment = MyDialogFragment.newInctance("MainActivity","لطفا به حساب کاربری خود وارد شوید" , "بستن" , R.drawable.warning2);
                            myDialogFragment.show(getFragmentManager(),"dialog");
                            navigationView.getMenu().getItem(0).setCheckable(false);
                        }else{
                            if(!internetConnection.haveNetworkConnection()){
                                intent = new Intent(getApplicationContext() , NoConnectionActivity.class);
                                intent.putExtra("activity" , 12);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                            else{
                                intent = new Intent(getApplicationContext() , UpdateProfileActivity.class);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        }
                        break;
                    case R.id.createStore :
                        if(!User.LOGIN_STATUS){
                            myDialogFragment = MyDialogFragment.newInctance("MainActivity","لطفا به حساب کاربری خود وارد شوید" , "بستن" , R.drawable.warning2);
                            myDialogFragment.show(getFragmentManager(),"dialog");
                            navigationView.getMenu().getItem(1).setCheckable(false);
                        }else {
                            if(User.SALESMAN_OR_NOT.equals("0")) {
                                if(!internetConnection.haveNetworkConnection()){
                                    intent = new Intent(getApplicationContext() , NoConnectionActivity.class);
                                    intent.putExtra("activity" , 13);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                                else{
                                    intent = new Intent(getApplicationContext(), StoreCreationActivity.class);
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            }
                            else{
                                if(!internetConnection.haveNetworkConnection()){
                                    intent = new Intent(getApplicationContext() , NoConnectionActivity.class);
                                    intent.putExtra("activity" , 14);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                                else{
                                    intent = new Intent(getApplicationContext(), StoreManagementActivity.class);
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            }
                        }

                        break;
                    case R.id.myOrders:
                        if(!User.LOGIN_STATUS){
                            myDialogFragment = MyDialogFragment.newInctance("MainActivity","لطفا به حساب کاربری خود وارد شوید" , "بستن" , R.drawable.warning2);
                            myDialogFragment.show(getFragmentManager(),"dialog");
                            navigationView.getMenu().getItem(2).setCheckable(false);
                        }else{
                            if(!internetConnection.haveNetworkConnection()){
                                intent = new Intent(getApplicationContext() , NoConnectionActivity.class);
                                startActivityForResult(intent,15);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                            else{
                                intent = new Intent(getApplicationContext() , MyOrderActivity.class);
                                startActivityForResult(intent , 0);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                drawerLayout.closeDrawers();
                            }
                        }
                        break;
                    case R.id.FAQ:
                        navigationView.getMenu().getItem(3).setCheckable(false);
                        break;
                    case R.id.about_us:
                        intent = new Intent(getApplicationContext() , AboutUsActivity.class);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case R.id.contact:
                        intent = new Intent(getApplicationContext() , ContactWithUsActivity.class);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case R.id.logOut:
                        if(User.LOGIN_STATUS) {
                            logout();
                        }
                        else {
                            finish();
                            System.exit(0);
                        }
                        break;
                    default:
                        break;
                }
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);
                return true;
            }
        });
    }
    public void logout(){
        String url = "http://" + Server.IP + ":" + Server.PORT + "/logOut.do?customerID=" + User.CUSTOMER_ID;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    if(response.equals("1")){
                        database = openOrCreateDatabase("Charsoo",MODE_PRIVATE,null);
                        database.beginTransaction();
                        database.execSQL("DELETE FROM Users");
                        database.setTransactionSuccessful();
                        database.endTransaction();
                        database.close();
                        finish();
                        System.exit(0);
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
        requestQueue.add(stringRequest);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}

