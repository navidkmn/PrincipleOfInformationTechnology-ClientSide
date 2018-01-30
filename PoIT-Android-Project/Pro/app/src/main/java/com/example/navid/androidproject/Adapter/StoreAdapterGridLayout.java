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

import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.StoreCompleteListActivity;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.Store;
import com.example.navid.androidproject.Other.TemporaryStoreProductsList;

public class StoreAdapterGridLayout extends RecyclerView.Adapter<StoreAdapterGridLayout.MyViewHolder> {

    private Context mContext;
    private List<Store> storeList;
    private WeakReference<StoreCompleteListActivity> activity;

    public StoreAdapterGridLayout(Context mContext, List<Store> storeList , StoreCompleteListActivity activity) {
        this.mContext = mContext;
        this.storeList = storeList;
        this.activity = new WeakReference<StoreCompleteListActivity>(activity);
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.storecardview_in_completelist_activity, parent, false);
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
//                activity.get().selectedStoreName = store.getName();
//                activity.get().selectedStoreImages = store.getImage();
//                TemporaryStoreProductsList.products = store.getProduct();
//                activity.get().selectedStoreSlogan = store.getSlogan();
//                activity.get().selectedStoreDescription = store.getDescription();
                activity.get().openNewActivity();
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

}
