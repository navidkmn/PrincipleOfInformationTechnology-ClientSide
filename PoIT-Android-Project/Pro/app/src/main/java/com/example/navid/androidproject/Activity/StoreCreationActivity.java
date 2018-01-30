package com.example.navid.androidproject.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.soundcloud.android.crop.Crop;

import com.example.navid.androidproject.R;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Other.JsonSpiceService;
import com.example.navid.androidproject.Other.MyCalendar;
import com.example.navid.androidproject.Other.PersianDetection;
import com.example.navid.androidproject.Other.Requests.ParlorEntryRequest;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StoreCreationActivity extends AppCompatActivity {

    static final int IMG_REQUEST = 1;

    static final int RETRY = 5;
    public int imageSedingTry;

    public ImageView _backArrow;
    public TextView _storeName;
    public EditText enterStoreName,_slogan,_description;
    public Button _save;
    public CheckBox isAccepted;
    public FloatingActionButton floatingActionButton;

    public ImageView storeImg;
    public FloatingActionButton deleteStoreImg;

    PersianDetection persianDetection;

    ProgressDialog progressDialog;

    RequestQueue rq,requestQueue;

    public String storeID;

    public Uri storeImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_creation);

        storeID = "";
        imageSedingTry = 0;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        rq = Volley.newRequestQueue(getApplicationContext());

        persianDetection = new PersianDetection();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        _storeName = (TextView) findViewById(R.id.name);
        enterStoreName = (EditText) findViewById(R.id.store);
        _slogan = (EditText) findViewById(R.id.slogan);
        _description = (EditText) findViewById(R.id.description);
        isAccepted = (CheckBox) findViewById(R.id.check);

        _backArrow = (ImageView) findViewById(R.id.backFromStoreCreation);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        storeImg = (ImageView) findViewById(R.id.storeImg);
        deleteStoreImg = (FloatingActionButton) findViewById(R.id.delete);
        deleteStoreImg.setVisibility(View.INVISIBLE);
        deleteStoreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storeImageUri != null){
                    storeImageUri = null;
                    storeImg.setImageURI(null);
                    deleteStoreImg.setVisibility(View.INVISIBLE);
                }
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (storeImageUri == null) {
                        pickImageFromGallery();
                    } else
                        Toast.makeText(getApplication(), "شما قبلا عکس خود را وارد کردید", Toast.LENGTH_SHORT).show();
                }
            });

        _save = (Button) findViewById(R.id.save);
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storeID.equals("")) {
                    if (isOkay()) {
                        _save.setEnabled(false);
                        progressDialog = new ProgressDialog(StoreCreationActivity.this, R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("ساخت حجره ... لطفا صبر کنید");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        storeCreation();
//                        StoreCreation storeCreation = new StoreCreation();
//                        storeCreation.execute();
                    }
                }
                else{
                    _save.setEnabled(false);
                    progressDialog = new ProgressDialog(StoreCreationActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("در حال ارسال عکس ... لطفا صبر کنید");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    sendRequest();
                }
            }
        });
    }

    public boolean isOkay(){
        boolean isOkay = true;

        if(isAccepted.isChecked()) {
            isAccepted.setError(null);
        }
        else{
            isAccepted.setError("");
            isOkay = false;
        }

        if (enterStoreName.getText().toString().isEmpty()) {
            enterStoreName.setError("لطفا نام حجره خود را وارد کنید");
            isOkay = false;
        } else {
            if (!persianDetection.textIsPersian(enterStoreName.getText().toString())) {
                enterStoreName.setError("لطفا نام حجره خود را فارسی وارد کنید");
                isOkay = false;
            } else {
                enterStoreName.setError(null);
            }
        }

        if (_slogan.getText().toString().isEmpty()) {
            _slogan.setError("لطفا شعار خود را وارد کنید");
            isOkay = false;
        } else {
            if (!persianDetection.textIsPersian(_slogan.getText().toString())) {
                _slogan.setError("لطفا شعار خود را فارسی وارد کنید");
                isOkay = false;
            } else {
                _slogan.setError(null);
            }
        }

        if (_description.getText().toString().isEmpty()) {
            _description.setError("لطفا اطلاعات حجره خود را وارد کنید");
            isOkay = false;
        } else {
            if (!persianDetection.textIsPersian(_description.getText().toString())) {
                _description.setError("لطفا اطلاعات حجره خود را فارسی وارد کنید");
                isOkay = false;
            } else {
                _description.setError(null);
            }
        }

        if(storeImageUri == null){
            isOkay = false;
            Toast.makeText(getApplicationContext(),"عکس حجره را وارد کنید",Toast.LENGTH_SHORT).show();
        }

        return isOkay;
    }

    public void pickImageFromGallery(){
        Crop.pickImage(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), StringGenerator.getSaltString()));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            storeImg.setImageURI(Crop.getOutput(result));
            storeImageUri = Crop.getOutput(result);
            deleteStoreImg.setVisibility(View.VISIBLE);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void updateInfo(){
        User.SALESMAN_OR_NOT = "1";
        User.STORE_ID = storeID;
        SQLiteDatabase database = openOrCreateDatabase("Charsoo", MODE_PRIVATE, null);
        database.beginTransaction();
        database.execSQL("UPDATE USERS SET salesmanOrNot = ? WHERE customerID = ?", new String[]{User.SALESMAN_OR_NOT ,User.CUSTOMER_ID});
        database.execSQL("UPDATE USERS SET storeID = ? WHERE customerID = ?", new String[]{User.STORE_ID,User.CUSTOMER_ID});
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            if(s.equals("1")){
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext() , StoreManagementActivity.class);
                intent.putExtra("activity","StoreCreation");
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
            else{
                if(imageSedingTry < RETRY){
                    sendRequest();
                    imageSedingTry++;
                }
                else {
                    _save.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "عکس ثبت نشد. بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if(imageSedingTry < RETRY){
                sendRequest();
                imageSedingTry++;
            }
            else {
                _save.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "عدم ارتباط با سرور برای ارسال عکس", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void sendRequest(){

        String url = "http://" + Server.IP + ":" + Server.PORT + "/insertParlorImage.do";
        Log.e("realmadrid" , url);

        try {
            InputStream UriInputStream = getContentResolver().openInputStream(storeImageUri);
            final String poster = BankBase64.encodeToString(IOUtils.toByteArray(UriInputStream), 0);

            StringRequest jor = new StringRequest(Request.Method.POST , url , responseListener , errorListener){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<String,String>();

                    JSONObject jo = new JSONObject();
                    jo.put("parlorID",storeID);
                    jo.put("poster" , poster);

                    param.put("JSON" , jo.toJSONString());
                    return param;
                }
            };
            rq.add(jor);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext() , "error" , Toast.LENGTH_SHORT).show();
        }
    }

    public void storeCreation(){
        String openingDate =  Long.toString(System.currentTimeMillis());
        String storeName = enterStoreName.getText().toString();
        String slogan = _slogan.getText().toString();
        String description  = _description.getText().toString();
        String sendSlogan = slogan.replace(' ','_');
        String sendDescription = description.replace(' ','_');

        String url = "http://" + Server.IP + ":" + Server.PORT + "/parlorEntry.do?" +
                "name="+ storeName +
                "&slogan="+ sendSlogan +
                "&description="+ sendDescription +
                "&openingDate="+ openingDate +
                "&salesmanID="+ User.CUSTOMER_ID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("0")) {
                    _save.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"نام حجره موردنظر موجود است. لطفا نام دیگری را انتخاب کنید",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    storeID = s;
                    updateInfo();
                    sendRequest();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                _save.setEnabled(true);
                Toast.makeText(getApplicationContext(),"عدم ارتباط با سرور",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        requestQueue.add(stringRequest);
    }

//    public class StoreCreation extends AsyncTask {
//
//        String openingDate =  Long.toString(System.currentTimeMillis());
//        String storeName = enterStoreName.getText().toString();
//        String slogan = _slogan.getText().toString();
//        String description  = _description.getText().toString();
//        String sendSlogan = slogan.replace(' ','_');
//        String sendDescription = description.replace(' ','_');
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/parlorEntry.do?" +
//                        "name="+ storeName +
//                        "&slogan="+ sendSlogan +
//                        "&description="+ sendDescription +
//                        "&openingDate="+ openingDate +
//                        "&salesmanID="+ User.CUSTOMER_ID);
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
//                String content = o.toString();
//                if(content.equals("0")){
//                    _save.setEnabled(true);
//                    Toast.makeText(getApplicationContext(),"نام حجره موردنظر موجود است. لطفا نام دیگری را انتخاب کنید",Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//                else {
//                    storeID = content;
//                    updateInfo();
//                    sendRequest();
//                }
//            } else {
//                _save.setEnabled(true);
//                Toast.makeText(getApplicationContext(),"عدم ارتباط با سرور",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//            }
//        }
//    }
}
