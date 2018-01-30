package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.util.ArrayList;

import com.example.navid.androidproject.Other.SubCategory;

public class SubCategoryListAdapter extends ArrayAdapter<SubCategory>{

    private Context context;
    private ArrayList<SubCategory> subCategories;

    public SubCategoryListAdapter(Context context , ArrayList<SubCategory> subCategories){
        super(context ,R.layout.subcategorylist , subCategories);
        this.context = context;
        this.subCategories = subCategories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.subcategorylist, null , true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.subCatText);
        txtTitle.setText(subCategories.get(position).name);
      //  Log.e("Navid" , subCategories.get(position).name);
        return rowView;
    }
}
