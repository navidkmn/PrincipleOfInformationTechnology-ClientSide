package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.navid.androidproject.R;

import butterknife.ButterKnife;
import com.example.navid.androidproject.Other.InternetConnection;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NoConnectionActivity extends AppCompatActivity {

    private static final int REQUEST_AppConnection = 0;
    private Button _retryButton;

    InternetConnection internetConnection;

    public enum Activity{
        MAIN,
        INTERESTS,
        PRODUCT,
        SEARCH,
        STORE,
        CARD,
        SUBCATEGORY,
        SUBSUBCATEGORY,
        SIGNUP,
        LOGIN,
        COMPLETELIST,
        STORECOMPLETELIST,
        UPDATEPROFILE,
        STORECREATION,
        STOREMANAGEMENT,
        MYORDER
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        super.onCreate(savedInstanceState);

        final Activity activity = convertToActivity(getIntent().getIntExtra("activity" , -1));

        setContentView(R.layout.activity_noconnection);
        ButterKnife.bind(this);

        _retryButton = (Button) findViewById(R.id.retry);
        internetConnection = new InternetConnection(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        _retryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()) {
                    Intent intent;
                    switch(activity){
                        case MAIN:
                            intent = new Intent(getApplicationContext() , MainActivity.class);
                            break;
                        case INTERESTS:
                            intent = new Intent(getApplicationContext() , InterestsActivity.class);
                            break;
                        case PRODUCT:
                            intent = new Intent(getApplicationContext() , ProductActivity.class);
                            int temp = getIntent().getIntExtra("pre" , -1);
                            if(temp == 1){
                                intent.putExtra("storeName" , getIntent().getStringExtra("storeName"));
                                intent.putExtra("storeImages" , getIntent().getIntegerArrayListExtra("storeImages"));
                                intent.putExtra("prepre" , getIntent().getIntExtra("prepre" , -1));
                            }
                            if(temp == 2){
                                intent.putExtra("subCategoryName" , getIntent().getStringExtra("subCategoryName"));
                            }
                            if(temp == 3){
                                intent.putExtra("completeListName" , getIntent().getStringExtra("completeListName"));
                            }
                            intent.putExtra("pre" , temp);
                            intent.putExtra("productImages", getIntent().getIntegerArrayListExtra("productImages"));
                            intent.putExtra("productName", getIntent().getStringExtra("productName").toString());
                            intent.putExtra("productPrice", getIntent().getStringExtra("productPrice").toString());
                            intent.putExtra("storeName" , getIntent().getStringExtra("storeName").toString());
                            break;
                        case SEARCH:
                            intent = new Intent(getApplicationContext() , SearchActivity.class);
                            break;
                        case STORE:
                            intent = new Intent(getApplicationContext() , StoreActivity.class);
                            int tempS = getIntent().getIntExtra("pre" , -1);
                            if(tempS == 2){
                                intent.putExtra("completeListName" , getIntent().getStringExtra("completeListName"));
                            }
                            if(tempS == 4){
                                intent.putExtra("prepre",getIntent().getIntExtra("prepre" , -1));
                            }
                            intent.putExtra("pre" , tempS);
                            intent.putExtra("storeImages", getIntent().getIntegerArrayListExtra("storeImages"));
                            intent.putExtra("storeName", getIntent().getStringExtra("storeName"));
                            break;
                        case CARD:
                            intent = new Intent(getApplicationContext() , CardActivity.class);
                            break;
                        case SUBCATEGORY:
                            intent = new Intent(getApplicationContext() , SubCategoryActivity.class);
                            intent.putExtra("categoryName" , getIntent().getStringExtra("categoryName"));
                            break;
                        case SUBSUBCATEGORY:
                            intent = new Intent(getApplicationContext() , SubSubCategoryActivity.class);
                            intent.putExtra("subCategoryName" , getIntent().getStringExtra("subCategoryName"));
                            break;
                        case SIGNUP:
                            intent = new Intent(getApplicationContext() , SignupActivity.class);
                            break;
                        case LOGIN:
                            intent = new Intent(getApplicationContext(), LoginActivity.class);
                            break;
                        case COMPLETELIST:
                            intent = new Intent(getApplicationContext() , CompleteListActivity.class);
                            intent.putExtra("completeListName" , getIntent().getStringExtra("completeListName"));
                            break;
                        case STORECOMPLETELIST:
                            intent = new Intent(getApplicationContext() , StoreCompleteListActivity.class);
                            intent.putExtra("completeListName" , getIntent().getStringExtra("completeListName"));
                            break;
                        case STORECREATION:
                            intent = new Intent(getApplicationContext(), StoreCreationActivity.class);
                            break;
                        case STOREMANAGEMENT:
                            intent = new Intent(getApplicationContext(), StoreManagementActivity.class);
                            break;
                        case UPDATEPROFILE:
                            intent = new Intent(getApplicationContext() , UpdateProfileActivity.class);
                            break;
                        case MYORDER:
                            intent = new Intent(getApplicationContext() , MyOrderActivity.class);
                            break;
                        default :
                            intent = null;
                            break;
                    }
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
    }

    private Activity convertToActivity(int result){
        switch(result){
            case 0 : return Activity.MAIN;
            case 1 : return Activity.INTERESTS;
            case 2 : return Activity.PRODUCT;
            case 3 : return Activity.SEARCH;
            case 4 : return Activity.STORE;
            case 5 : return Activity.CARD;
            case 6 : return Activity.SUBCATEGORY;
            case 7 : return  Activity.SUBSUBCATEGORY;
            case 8 : return Activity.SIGNUP;
            case 9 : return Activity.LOGIN;
            case 10 : return Activity.COMPLETELIST;
            case 11 : return Activity.STORECOMPLETELIST;
            case 12 : return Activity.UPDATEPROFILE;
            case 13 : return  Activity.STORECREATION;
            case 14 : return Activity.STOREMANAGEMENT;
            case 15 : return Activity.MYORDER;
            default : return null;
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

    }
}
