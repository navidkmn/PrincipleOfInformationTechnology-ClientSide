package com.example.navid.androidproject.Adapter;
import com.example.navid.androidproject.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.lang.ref.WeakReference;
import java.util.List;

import com.example.navid.androidproject.Activity.InterestsActivity;
import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.StoreActivity;
import com.example.navid.androidproject.Activity.SubSubCategoryActivity;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.StaticInfo;

public class FavoriteProductAdapter extends RecyclerView.Adapter<FavoriteProductAdapter.MyViewHolder> {

    private Context mContext;
    private List<Product> productList;
    private WeakReference<InterestsActivity> iactivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage,remove;

        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.nameProduct);
            productPrice = (TextView) view.findViewById(R.id.priceProduct);
            productImage = (ImageView) view.findViewById(R.id.imgProduct);
            remove = (ImageView) view.findViewById(R.id.remove);
        }
    }

    public FavoriteProductAdapter(Context mContext , List<Product> productList , InterestsActivity iactivity){
        this.mContext = mContext;
        this.productList = productList;
        this.iactivity = new WeakReference<InterestsActivity>(iactivity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_product_cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Product product = productList.get(position);
        final String productStore = product.getStoreName();
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        holder.productImage.setImageURI(product.getImage().get(0));

        Log.e("ImageUri" , product.getImage().get(0).toString());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iactivity.get().deleteProduct(holder.getAdapterPosition() , holder.productName.getText().toString() , productStore);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticInfo.SAVED_PRODUCT = product;
                iactivity.get().openProductNewActivity();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}

