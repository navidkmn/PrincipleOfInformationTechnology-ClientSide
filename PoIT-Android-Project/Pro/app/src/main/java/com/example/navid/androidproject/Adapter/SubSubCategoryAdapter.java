package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.example.navid.androidproject.Activity.SubSubCategoryActivity;
import com.example.navid.androidproject.Other.StaticInfo;

public class SubSubCategoryAdapter extends RecyclerView.Adapter<SubSubCategoryAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> subSubCategoryNameList;
    private WeakReference<SubSubCategoryActivity> activity;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView subSubCategoryName;

    public MyViewHolder(View view) {
        super(view);
        subSubCategoryName = (TextView) view.findViewById(R.id.subSubCategoryText);
    }
}

    public SubSubCategoryAdapter(Context mContext, ArrayList<String> subSubCategoryNameList , SubSubCategoryActivity activity) {
        this.mContext = mContext;
        this.subSubCategoryNameList = subSubCategoryNameList;
        this.activity = new WeakReference<SubSubCategoryActivity>(activity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subsubcategorycardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String subSubCategory = subSubCategoryNameList.get(position);
        holder.subSubCategoryName.setText(subSubCategory);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticInfo.SAVED_SUB_SUB_CATEGORY = subSubCategory;
            }
        });
    }

    @Override
    public int getItemCount() {
        return subSubCategoryNameList.size();
    }

}
