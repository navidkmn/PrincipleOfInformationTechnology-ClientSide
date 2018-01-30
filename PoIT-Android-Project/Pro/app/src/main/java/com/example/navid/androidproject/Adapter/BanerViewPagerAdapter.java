package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.navid.androidproject.R;

import java.util.ArrayList;

public class BanerViewPagerAdapter extends PagerAdapter {
    private Context contex;
    private LayoutInflater layoutInflater;
    int [] layouts;
    ArrayList<Uri> images;

    public BanerViewPagerAdapter(Context contex,ArrayList<Uri> images) {
        this.contex = contex;
        this.images = images;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.main_baner, container, false);
        container.addView(view);
        ImageView img = (ImageView) view.findViewById(R.id.banerImg);
        img.setRotationY(180);
        img.setImageURI(images.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
