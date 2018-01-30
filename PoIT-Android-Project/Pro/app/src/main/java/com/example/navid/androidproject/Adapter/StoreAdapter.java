package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.lang.ref.WeakReference;
import java.util.List;

import com.example.navid.androidproject.Activity.InterestsActivity;
import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Fragment.StoreFragment;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.Store;
import com.example.navid.androidproject.Other.TemporaryStoreProductsList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

    private Context mContext;
    private List<Store> storeList;
    private WeakReference<StoreFragment> fragment;
    private WeakReference<InterestsActivity> iactivity;
    private String activityOrFragment = "";

    public StoreAdapter(Context mContext, List<Store> storeList , StoreFragment fragment) {
        this.mContext = mContext;
        this.storeList = storeList;
        this.fragment = new WeakReference<StoreFragment>(fragment);
        activityOrFragment = "fragment";
    }

    public StoreAdapter (Context mContext , List<Store> storeList , InterestsActivity iactivity){
        this.mContext = mContext;
        this.storeList = storeList;
        this.iactivity = new WeakReference<InterestsActivity>(iactivity);
        activityOrFragment = "iactivity";
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView storeName;
        public ImageView storeImage;

        public MyViewHolder(View view) {
            super(view);
            storeName = (TextView) view.findViewById(R.id.nameStore);
            storeImage = (ImageView) view.findViewById(R.id.imgStore);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.storecardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Store store = storeList.get(position);
        holder.storeName.setText(store.getName());
        holder.storeImage.setImageURI(store.getImage().get(0));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticInfo.SAVED_STORE = store;
                if(activityOrFragment == "fragment") {
//                    fragment.get().selectedStoreName = store.getName();
//                    fragment.get().selectedStoreImages = store.getImage();
//                    TemporaryStoreProductsList.products = store.getProduct();
//                    fragment.get().selectedStoreSlogan = store.getSlogan();
//                    fragment.get().selectedStoreDescription = store.getDescription();
                    fragment.get().openNewActivity();
                }
                if(activityOrFragment == "iactivity"){
//                    iactivity.get().selectedStoreName = store.getName();
//                    iactivity.get().selectedStoreImages = store.getImage();
//                    TemporaryStoreProductsList.products = store.getProduct();
//                    iactivity.get().selectedStoreSlogan = store.getSlogan();
//                    iactivity.get().selectedStoreDescription = store.getDescription();
                    iactivity.get().openStoreNewActivity();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

}
