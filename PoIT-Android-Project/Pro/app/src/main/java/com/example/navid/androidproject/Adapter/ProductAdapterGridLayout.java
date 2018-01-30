package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.lang.ref.WeakReference;
import java.util.List;

import com.example.navid.androidproject.Activity.CompleteListActivity;
import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.SearchActivity;
import com.example.navid.androidproject.Activity.StoreActivity;
import com.example.navid.androidproject.Activity.SubSubCategoryActivity;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Listener.OnLoadMoreListener;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.StaticInfo;

public class ProductAdapterGridLayout extends RecyclerView.Adapter<ProductAdapterGridLayout.MyViewHolder> {

    private Context mContext;
    private List<Product> productList;
    private WeakReference<StoreActivity> factivity;
    private WeakReference<SubSubCategoryActivity> sactivity;
    private WeakReference<CompleteListActivity> cactivity;
    private WeakReference<SearchActivity> searchActivity;
    private String activityOrFragment = "";

    static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;

        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.nameProduct);
            productPrice = (TextView) view.findViewById(R.id.priceProduct);
            productImage = (ImageView) view.findViewById(R.id.imgProduct);
        }
    }

    public ProductAdapterGridLayout(Context mContext, List<Product> productList , SearchActivity searchActivity) {
        this.mContext = mContext;
        this.productList = productList;
        this.searchActivity = new WeakReference<SearchActivity>(searchActivity);
        this.activityOrFragment = "searchActivity";
    }

    public ProductAdapterGridLayout(Context mContext , List<Product> productList , StoreActivity factivity){
        this.mContext = mContext;
        this.productList = productList;
        this.factivity = new WeakReference<StoreActivity>(factivity);
        this.activityOrFragment = "factivity";
    }


    public ProductAdapterGridLayout(Context mContext , List<Product> productList , CompleteListActivity cactivity){
        this.mContext = mContext;
        this.productList = productList;
        this.cactivity = new WeakReference<CompleteListActivity>(cactivity);
        this.activityOrFragment = "cactivity";
    }

    public ProductAdapterGridLayout(Context mContext , List<Product> productList , SubSubCategoryActivity sactivity){
        this.mContext = mContext;
        this.productList = productList;
        this.sactivity = new WeakReference<SubSubCategoryActivity>(sactivity);
        this.activityOrFragment = "sactivity";
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productcardview_in_store_completelist_activity, parent, false);
            return new MyViewHolder(itemView);
   }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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

                if(activityOrFragment == "searchActivity") {
                    searchActivity.get().openNewActivity();
                }

                if (activityOrFragment == "factivity") {
                    factivity.get().openNewActivity();
                }

                if (activityOrFragment == "sactivity") {
                    sactivity.get().openNewActivity();
                }

                if (activityOrFragment == "cactivity") {
                    cactivity.get().openNewActivity();
                }
                }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
