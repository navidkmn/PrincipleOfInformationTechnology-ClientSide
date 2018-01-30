package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.navid.androidproject.R;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.example.navid.androidproject.Adapter.FavoriteProductAdapter;
import com.example.navid.androidproject.Adapter.FavoriteStoreAdapter;
import com.example.navid.androidproject.Adapter.ProductAdapter;
import com.example.navid.androidproject.Fragment.MyDialogFragment;
import com.example.navid.androidproject.Other.CartList;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.Store;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InterestsActivity extends AppCompatActivity {

    private static final int REQUEST_AppInterests = 0;

    public ImageView _back;

    public RecyclerView productRecyclerView;
    public RecyclerView storeRecyclerView;

    public FavoriteProductAdapter productAdapter;
    public FavoriteStoreAdapter storeAdapter;

    public InternetConnection internetConnection;

    public SQLiteDatabase favoriteProduct;
    public SQLiteDatabase favoriteStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        internetConnection = new InternetConnection(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        Cursor cursor;

        CartList.FAVORITE_PRODUCT_LIST = new ArrayList<>();
        favoriteProduct = openOrCreateDatabase("favoriteProduct" , MODE_PRIVATE , null);
        favoriteProduct.beginTransaction();
        cursor = favoriteProduct.rawQuery("SELECT * FROM favoriteProduct" , null);
        while (cursor.moveToNext()){
            //    StringArrayList.convertStringArrayListToUriArrayList(StringArrayList.convertStringToArrayList(cursor.getString(cursor.getColumnIndex("images"))) , getApplicationContext().getCacheDir()),
            ArrayList<Uri> images = new ArrayList<>();
            for(int i = 1 ; i <= 5 ; i++) {
                if (cursor.getString(cursor.getColumnIndex("image" + i)).equals("null")) ;
                else{
                    images.add(Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex("image" + i)))));
                }
            }
           CartList.FAVORITE_PRODUCT_LIST.add(
                   new Product(
                           cursor.getString(cursor.getColumnIndex("id")),
                           cursor.getString(cursor.getColumnIndex("name")),
                           cursor.getString(cursor.getColumnIndex("price")),
                           cursor.getString(cursor.getColumnIndex("offPrice")),
                           cursor.getString(cursor.getColumnIndex("storeID")),
                           cursor.getString(cursor.getColumnIndex("storeName")),
                           images,
                           cursor.getString(cursor.getColumnIndex("quantity")),
                           cursor.getString(cursor.getColumnIndex("description")),
                           cursor.getString(cursor.getColumnIndex("subCategory"))
                   )
           );
        }

        favoriteProduct.setTransactionSuccessful();
        favoriteProduct.endTransaction();
        favoriteProduct.close();

        CartList.FAVORITE_STORE_LIST = new ArrayList<>();
        favoriteStore = openOrCreateDatabase("favoriteStore" , MODE_PRIVATE , null);
        favoriteStore.beginTransaction();
        cursor = favoriteStore.rawQuery("SELECT * FROM favoriteStore" , null);
        while (cursor.moveToNext()){
            File file = new File(cursor.getString(cursor.getColumnIndex("images")));
            ArrayList<Uri> pics = new ArrayList<>();
            pics.add(Uri.fromFile(file));
            //  StringArrayList.convertStringArrayListToUriArrayList(StringArrayList.convertStringToArrayList(cursor.getString(cursor.getColumnIndex("images"))) , getApplicationContext().getCacheDir()),
            CartList.FAVORITE_STORE_LIST.add(
                     new Store(
                            cursor.getString(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            pics,
                            cursor.getString(cursor.getColumnIndex("salesmanID")),
                            cursor.getString(cursor.getColumnIndex("slogan")),
                            cursor.getString(cursor.getColumnIndex("description")),
                            cursor.getString(cursor.getColumnIndex("openingDate")),
                            StringArrayList.convertStringToProductArrayList(cursor.getString(cursor.getColumnIndex("products")) , getApplicationContext().getCacheDir())
                    )
            );
        }
        favoriteStore.setTransactionSuccessful();
        favoriteStore.endTransaction();
        favoriteStore.close();

        _back = (ImageView) findViewById(R.id.backFromInterests);
        _back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivityForResult(intent, REQUEST_AppInterests);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity" , 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        productRecyclerView = (RecyclerView) findViewById(R.id.favoriteProductRecyclerView);

        productAdapter = new FavoriteProductAdapter(getApplicationContext() , CartList.FAVORITE_PRODUCT_LIST , this);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.HORIZONTAL ,true));
        productRecyclerView.setAdapter(productAdapter);

        storeRecyclerView = (RecyclerView) findViewById(R.id.favoriteStoreRecyclerView);

        storeAdapter = new FavoriteStoreAdapter(getApplicationContext() , CartList.FAVORITE_STORE_LIST , this);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.HORIZONTAL , true));
        storeRecyclerView.setAdapter(storeAdapter);
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

    public void doPositiveClick(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void openProductNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra("pre" , 4);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            intent.putExtra("activity" , 1);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    public void openStoreNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
            intent.putExtra("pre" , 3);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            intent.putExtra("activity" , 1);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    public void deleteProduct(int position , String name , String store){

        favoriteProduct = openOrCreateDatabase("favoriteProduct" , MODE_PRIVATE , null);
        favoriteProduct.beginTransaction();
        favoriteProduct.execSQL("DELETE FROM favoriteProduct WHERE name = ? AND storeName = ?",new String[]{name,store});
        favoriteProduct.setTransactionSuccessful();
        favoriteProduct.endTransaction();
        favoriteProduct.close();

        CartList.FAVORITE_PRODUCT_LIST.remove(position);
        ((ViewGroup)productRecyclerView.getChildAt(position).getParent()).removeView(productRecyclerView.getChildAt(position));
        productAdapter.notifyItemRemoved(position);
    }

    public void deleteStore(int position , String name){

        favoriteStore = openOrCreateDatabase("favoriteStore" , MODE_PRIVATE , null);
        favoriteStore.beginTransaction();
        favoriteStore.execSQL("DELETE FROM favoriteStore WHERE name = ?",new String[]{name});
        favoriteStore.setTransactionSuccessful();
        favoriteStore.endTransaction();
        favoriteStore.close();

        CartList.FAVORITE_STORE_LIST.remove(position);
        ((ViewGroup)storeRecyclerView.getChildAt(position).getParent()).removeView(storeRecyclerView.getChildAt(position));
        storeAdapter.notifyItemRemoved(position);
    }
}
