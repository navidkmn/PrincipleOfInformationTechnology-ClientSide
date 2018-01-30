package com.example.navid.androidproject.Activity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.navid.androidproject.Adapter.BanerViewPagerAdapter;
import com.example.navid.androidproject.Other.StaticInfo;

import com.example.navid.androidproject.R;

public class ZoomActivity extends AppCompatActivity {

    ArrayList<Uri> productImages;
    int item;

    private ViewPager banerViewPager;
    private BanerViewPagerAdapter banerViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    public ImageView _exit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        productImages = StaticInfo.SAVED_PRODUCT.getImage();
        item = getIntent().getIntExtra("item" , -1);

        Log.e("item" , Integer.toString(item));

       _exit = (ImageView) findViewById(R.id.closeeeer);
        _exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext() , ProductActivity.class);
//                intent.putExtra("pre" , getIntent().getIntExtra("pre" , -1));
//                intent.putExtra("productName" , getIntent().getStringExtra("productName").toString());
//                intent.putExtra("productPrice" , getIntent().getStringExtra("productPrice").toString());
//                intent.putExtra("productImages" ,getIntent().getIntegerArrayListExtra("productImages"));
                Intent intent = new Intent();
                setResult(RESULT_OK , intent);
                finish();
          //      startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        banerViewPager = (ViewPager) findViewById(R.id.baner);
        dotsLayout = (LinearLayout) findViewById(R.id.banerDots);

        banerViewPager.setCurrentItem(productImages.size() - 1);
        banerViewPager.setRotationY(180);

        addBottomDots(productImages.size() - 1);
        banerViewPagerAdapter = new BanerViewPagerAdapter(getApplicationContext(), productImages);
        banerViewPager.setAdapter(banerViewPagerAdapter);
        banerViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK , intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[productImages.size()];

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
            addBottomDots(productImages.size() - 1 - position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
}
