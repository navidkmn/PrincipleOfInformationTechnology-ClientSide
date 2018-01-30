package com.example.navid.androidproject.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.navid.androidproject.Other.MyCalendar;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navid.androidproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WriteCommentActivity extends AppCompatActivity {

    ImageView _backArrow;
    Button _save;
    EditText _comment;
    RatingBar _rate,_rate2;

    ProgressDialog progressDialog;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        _comment = (EditText) findViewById(R.id.writeComment);
        _rate = (RatingBar) findViewById(R.id.ratingBar);
        _rate2 = (RatingBar) findViewById(R.id.ratingBar2);

        _backArrow = (ImageView) findViewById(R.id.backFromWriteReview);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED , intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _save = (Button) findViewById(R.id.saveComment);
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(_comment.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),"لطفا نظر خود را وارد کنید",Toast.LENGTH_SHORT).show();
            }else{
                if(Float.toString(_rate.getRating()).equals("0.0")){
                    Toast.makeText(getApplicationContext(),"لطفا به قیمت امتیاز دهید",Toast.LENGTH_SHORT).show();
                }else{
                    if(Float.toString(_rate2.getRating()).equals("0.0")) {
                        Toast.makeText(getApplicationContext(), "لطفا به کیفیت امتیاز دهید", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        _save.setEnabled(false);
                        sendComment();
                        progressDialog = new ProgressDialog(WriteCommentActivity.this, R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setMessage("ارسال نظر ... لطفا صبر کنید");
                        progressDialog.show();
                    }
                }
            }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED , intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void sendComment(){

        String comment = _comment.getText().toString().replace(' ','_');
        String ecorating = Integer.toString((int)_rate.getRating());
        String quarating = Integer.toString((int)_rate2.getRating());
        String submitDate = Long.toString(System.currentTimeMillis());

        String url = "http://" + Server.IP + ":" + Server.PORT + "/submitReview.do?" +
                "submitDate="+submitDate +
                "&customerID="+ User.CUSTOMER_ID+
                "&productID="+ StaticInfo.SAVED_PRODUCT.getID()+
                "&ecorating="+ecorating+
                "&quarating="+quarating+
                "&reviewText="+comment;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    if(response.equals("1")){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"درخواست شما با موفقیت ثبت شد",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    else{
                        _save.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    _save.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"خطا",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _save.setEnabled(true);
                Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

//    public class SendComment extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            String comment = _comment.getText().toString().replace(' ','_');
//            String ecorating = Integer.toString((int)_rate.getRating());
//            String quarating = Integer.toString((int)_rate2.getRating());
//            String submitDate = Long.toString(System.currentTimeMillis());
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/submitReview.do?" +
//                        "submitDate="+submitDate +
//                        "&customerID="+ User.CUSTOMER_ID+
//                        "&productID="+ StaticInfo.SAVED_PRODUCT.getID()+
//                        "&ecorating="+ecorating+
//                        "&quarating="+quarating+
//                        "&reviewText="+comment);
//
//                URLConnection urlConnection = url.openConnection();
//                InputStream inputStream = urlConnection.getInputStream();
//                int ascii = inputStream.read();
//                String content = "";
//                while (ascii != -1) {
//                    content += (char) ascii;
//                    ascii = inputStream.read();
//                }
//                inputStream.close();
//                return content;
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//
//            if (o != null) {
//                if(o.toString().equals("1")){
//                    Toast.makeText(getApplicationContext(),"درخواست شما با موفقیت ثبت شد",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//            } else {
//
//            }
//        }
//    }
}
