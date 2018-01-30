package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.lang.ref.WeakReference;
import java.util.List;

import com.example.navid.androidproject.Activity.CardActivity;
import com.example.navid.androidproject.Other.Product;
import com.example.navid.androidproject.Other.ShoppingBagProduct;

public class CartAdapter extends  RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context mContext;
    private List<ShoppingBagProduct> productList;
    private WeakReference<CardActivity> iactivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice,productStore,productQuantity;
        public ImageView productImage,deleteProduct;
        public ImageView addProduct,removeProduct;
        public int number = 1;

        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.nameProduct);
            productPrice = (TextView) view.findViewById(R.id.priceProduct);
            productStore = (TextView) view.findViewById(R.id.storeProduct);
            productImage = (ImageView) view.findViewById(R.id.imgProduct);
            productQuantity = (TextView) view.findViewById(R.id.quantityProduct);
            addProduct = (ImageView) view.findViewById(R.id.addProduct);
            removeProduct = (ImageView) view.findViewById(R.id.removeProduct);
            deleteProduct = (ImageView) view.findViewById(R.id.deleteProduct);
        }
    }

    public CartAdapter(Context mContext , List<ShoppingBagProduct> productList , CardActivity iactivity){
        this.mContext = mContext;
        this.productList = productList;
        this.iactivity = new WeakReference<CardActivity>(iactivity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ShoppingBagProduct product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getProductPrice());
        holder.productStore.setText(product.getProductStore());
        holder.productQuantity.setText(product.getProductQuantity());
        holder.productImage.setImageURI(product.getProductImage());

        holder.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.number++;
                holder.productQuantity.setText(Integer.toString(holder.number));
                iactivity.get().updateSQLite_Cart(holder.number , holder.productName.getText().toString() , holder.productStore.getText().toString());
            }
        });

        holder.removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.number > 1){
                    holder.number--;
                    holder.productQuantity.setText(Integer.toString(holder.number));
                    iactivity.get().updateSQLite_Cart(holder.number , holder.productName.getText().toString() , holder.productStore.getText().toString());
                }
            }
        });

        holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iactivity.get().deleteProduct(holder.getAdapterPosition(), holder.productName.getText().toString(),holder.productStore.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
