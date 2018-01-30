package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;

import com.example.navid.androidproject.Activity.InterestsActivity;
import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.ProductCreationActivity;
import com.example.navid.androidproject.Activity.StoreActivity;
import com.example.navid.androidproject.Activity.SubSubCategoryActivity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Fragment.HomeFragment;
import com.example.navid.androidproject.Other.Product;

public class ProductCreationAdapter extends RecyclerView.Adapter<ProductCreationAdapter.MyViewHolder> {

    private Context mContext;
    private List<Uri> productImages;

    private WeakReference<ProductCreationActivity> pcactivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public FloatingActionButton deleteImg;
        public ImageView productImage;

        public MyViewHolder(View view) {
            super(view);
            deleteImg = (FloatingActionButton) view.findViewById(R.id.delete);
            productImage = (ImageView) view.findViewById(R.id.imgProduct);
        }
    }

    public ProductCreationAdapter(Context mContext, List<Uri> productImages , ProductCreationActivity pcactivity) {
        this.mContext = mContext;
        this.productImages = productImages;
        this.pcactivity = new WeakReference<ProductCreationActivity>(pcactivity);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_image_cardview_product_creation_activity, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        
        final Uri productImg = productImages.get(holder.getAdapterPosition());
        holder.productImage.setImageURI(productImg);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcactivity.get().deleteProductImage(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productImages.size();
    }

}
