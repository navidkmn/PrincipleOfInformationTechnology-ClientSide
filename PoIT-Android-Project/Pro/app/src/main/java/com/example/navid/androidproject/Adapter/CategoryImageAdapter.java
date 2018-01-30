package com.example.navid.androidproject.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.navid.androidproject.R;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.example.navid.androidproject.Fragment.CategoryFragment;
import com.example.navid.androidproject.Other.Category;
import com.example.navid.androidproject.Other.StaticInfo;

public class CategoryImageAdapter extends RecyclerView.Adapter<CategoryImageAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<Category> categoryList;
    private WeakReference<CategoryFragment> fragment;
    // Keep all Images in array

    // Constructor
    public CategoryImageAdapter(Context c, ArrayList<Category> categoryList, CategoryFragment fragment){
        mContext = c;
        this.categoryList = categoryList;
        this.fragment = new WeakReference<CategoryFragment>(fragment);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView categoryName;
        public ImageView categoryImage;

        public MyViewHolder(View view) {
            super(view);
            categoryImage = (ImageView) view.findViewById(R.id.imgCategory);
            categoryName = (TextView) view.findViewById(R.id.nameCategory);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorycardview,parent ,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        Glide.with(mContext).load(category.getImage()).into(holder.categoryImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticInfo.SAVED_CATEGORY = category;
                fragment.get().openNewActivity();
            }
        });
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}


