package com.example.navid.androidproject.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.navid.androidproject.R;
import com.example.navid.androidproject.Other.InternetConnection;

public class StartActivity extends AppCompatActivity {

    InternetConnection internetConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        internetConnection = new InternetConnection(this);
        if(internetConnection.haveNetworkConnection()){
            Intent intent = new Intent(getApplicationContext() , MainActivity.class);
            startActivity(intent);
            finish();
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
}
