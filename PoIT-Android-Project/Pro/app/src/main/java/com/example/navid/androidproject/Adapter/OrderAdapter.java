package com.example.navid.androidproject.Adapter;

import android.content.Context;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.example.navid.androidproject.Activity.MyOrderActivity;
import com.example.navid.androidproject.Other.Order;

public class OrderAdapter extends  RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<Order> orders;
    private WeakReference<MyOrderActivity> iactivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView price;
        TextView offPrice;
        TextView storeName;
        ImageView image;
        TextView quantity;
        TextView orderStatus;
        TextView submitDate;

        public MyViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.nameProduct);
            price  = (TextView) view.findViewById(R.id.priceProduct);
            offPrice = (TextView) view.findViewById(R.id.offPriceProduct);
            storeName = (TextView) view.findViewById(R.id.storeProduct);
            image = (ImageView) view.findViewById(R.id.imgProduct);
            quantity = (TextView) view.findViewById(R.id.quantityProduct);
            orderStatus = (TextView) view.findViewById(R.id.orderStatus);
            submitDate = (TextView) view.findViewById(R.id.submitDate);
        }
    }

    public OrderAdapter(Context mContext , List<Order> orders , MyOrderActivity iactivity){
        this.mContext = mContext;
        this.orders = orders;
        this.iactivity = new WeakReference<MyOrderActivity>(iactivity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Order order = orders.get(position);
        holder.productName.setText("نام کالا: " + order.getProductName());
        holder.price.setText("قیمت کالا: " + order.getPrice());
        holder.offPrice.setText("قیمت تخفیف کالا: " + order.getOffPrice());
        holder.storeName.setText("نام حجره: " + order.getStoreName());
        holder.image.setImageURI(order.getImage());
        holder.quantity.setText("تعداد کالا: " + order.getQuantity());
        holder.orderStatus.setText("وضعیت سفارش: " + order.getOrderStatus());
        holder.submitDate.setText("زمان ثبت سفارش: " + getDate(Long.parseLong(order.getSubmitDate()) , "yyyy/MM/dd"));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

