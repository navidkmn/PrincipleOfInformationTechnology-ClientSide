package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class FavoriteStoreAdapter extends RecyclerView.Adapter<FavoriteStoreAdapter.MyViewHolder> {

    private Context mContext;
    private List<Store> storeList;
    private WeakReference<InterestsActivity> iactivity;

    public FavoriteStoreAdapter (Context mContext , List<Store> storeList , InterestsActivity iactivity){
        this.mContext = mContext;
        this.storeList = storeList;
        this.iactivity = new WeakReference<InterestsActivity>(iactivity);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView storeName;
        public ImageView storeImage,remove;

        public MyViewHolder(View view) {
            super(view);
            storeName = (TextView) view.findViewById(R.id.nameStore);
            storeImage = (ImageView) view.findViewById(R.id.imgStore);
            remove = (ImageView) view.findViewById(R.id.remove);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_store_cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Store store = storeList.get(position);
        holder.storeName.setText(store.getName());

        holder.storeImage.setImageURI(store.getImage().get(0));

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iactivity.get().deleteStore(holder.getAdapterPosition() , holder.storeName.getText().toString());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticInfo.SAVED_STORE = store;
                iactivity.get().openStoreNewActivity();
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

}
