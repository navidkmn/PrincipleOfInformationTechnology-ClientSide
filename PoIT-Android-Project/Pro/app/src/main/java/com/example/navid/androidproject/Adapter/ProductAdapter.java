package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.example.navid.androidproject.Activity.StoreManagementActivity;
import com.example.navid.androidproject.Activity.SubSubCategoryActivity;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.StaticInfo;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context mContext;
    private List<Product> productList;
    private WeakReference<HomeFragment> fragment;
    private WeakReference<InterestsActivity> iactivity;
    private WeakReference<StoreManagementActivity> smactivity;
   // private WeakReference<StoreActivity> factivity;
   // private WeakReference<SubSubCategoryActivity> sactivity;
    private String activityOrFragment = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;

        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.nameProduct);
            productPrice = (TextView) view.findViewById(R.id.priceProduct);
            productImage = (ImageView) view.findViewById(R.id.imgProduct);
        }
    }

    public ProductAdapter(Context mContext, List<Product> productList , HomeFragment fragment) {
        this.mContext = mContext;
        this.productList = productList;
        this.fragment = new WeakReference<HomeFragment>(fragment);
        this.activityOrFragment = "fragment";
    }

    public ProductAdapter(Context mContext , List<Product> productList , InterestsActivity iactivity){
        this.mContext = mContext;
        this.productList = productList;
        this.iactivity = new WeakReference<InterestsActivity>(  iactivity);
        this.activityOrFragment = "iactivity";
    }

    public ProductAdapter(Context mContext, List<Product> productList , StoreManagementActivity smactivity) {
        this.mContext = mContext;
        this.productList = productList;
        this.smactivity = new WeakReference<StoreManagementActivity>(smactivity);
        this.activityOrFragment = "smactivity";
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productcardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        if(!product.getImage().isEmpty())
            holder.productImage.setImageURI(product.getImage().get(0));
        else{
            holder.productImage.setImageURI(null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StaticInfo.SAVED_PRODUCT = product;

                if(activityOrFragment == "fragment") {
                    fragment.get().openNewActivity();
                }
                if(activityOrFragment == "iactivity"){
                    iactivity.get().openProductNewActivity();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
