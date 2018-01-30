package com.example.navid.androidproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.navid.androidproject.R;

import com.example.navid.androidproject.Other.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Comment> commentList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView sender,description,date;
        public RatingBar quality,price;

        public MyViewHolder(View view){
            super(view);
            sender = (TextView) view.findViewById(R.id.sender);
            description = (TextView) view.findViewById(R.id.comment);
            date = (TextView) view.findViewById(R.id.date);
            quality = (RatingBar) view.findViewById(R.id.qualityRatingBar);
            price = (RatingBar) view.findViewById(R.id.priceRatingBar);
        }
    }

    public CommentAdapter(Context context , ArrayList<Comment> commentList){
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Comment comment = commentList.get(position);
        holder.sender.setText(comment.getSender());
        holder.description.setText(comment.getDescription());
        holder.date.setText(getDate(Long.parseLong(comment.getSubmitdate()) , "yyyy/MM/dd"));
        holder.quality.setRating(Float.parseFloat(comment.getQs()));
        holder.price.setRating(Float.parseFloat(comment.getEs()));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
