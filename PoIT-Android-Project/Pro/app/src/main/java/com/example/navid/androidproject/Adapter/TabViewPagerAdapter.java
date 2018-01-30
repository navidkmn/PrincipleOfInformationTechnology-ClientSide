package com.example.navid.androidproject.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import com.example.navid.androidproject.Fragment.CategoryFragment;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Fragment.StoreFragment;

public class TabViewPagerAdapter extends FragmentPagerAdapter {

 ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static int ITEM_NO = 3;

    public TabViewPagerAdapter(FragmentManager manager){
        super(manager);
    }

    @Override
    public Fragment getItem (int position){
        switch (position){
            case 1:
                return new HomeFragment();
                        //HomeFragment.newInstance(1,"خانه");
            case 0:
                return new CategoryFragment();
                        //CategoryFragment.newInstance(0,"دسته‌بندی");
            case 2:
                return new StoreFragment();
                        //StoreFragment.newInstance(2,"حجره‌ها");
        }
        return null;
    }

    @Override
    public int getCount(){
        return ITEM_NO;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 1:
                return "خانه";
            case 0:
                return "دسته‌بندی";
            case 2:
                return "حجره‌ها";
        }
        return null;
    }
}
