package com.example.navid.androidproject.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.text.LoginFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navid.androidproject.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.spec.ECField;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import com.example.navid.androidproject.AndroidEncoderUtil.*;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.NumberDetection;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    ProgressDialog progressDialog;

    InternetConnection internetConnection;
    NumberDetection numberDetection;
    MD5 md5;

    SQLiteDatabase database;

    private EditText _emailOrMoblieText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;

    RequestQueue requestQueue;

    String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        internetConnection = new InternetConnection(this);
        numberDetection = new NumberDetection();
        md5 = new MD5();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        _emailOrMoblieText = (EditText) findViewById(R.id.input_email_mobile);
        _emailOrMoblieText.requestFocus();

        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordText.setTransformationMethod(new PasswordTransformationMethod());

        _loginButton = (Button) findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection())
                    login();
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity",9);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        _signupLink = (TextView) findViewById(R.id.link_signup);
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("لطفا منتظر بمانید");
        progressDialog.show();

        sendLogin();
//        Login login = new Login();
//        login.execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        finish();
        startActivity(intent);
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "ورود ناموفق", Toast.LENGTH_SHORT).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String emailOrMoblie = _emailOrMoblieText.getText().toString();
        String password = _passwordText.getText().toString();

        if (emailOrMoblie.isEmpty()){
            _emailOrMoblieText.setError("لطفا آدرس ایمیل و یا شماره تلفن خود را وارد کنید");
            valid = false;
        } else {
            if(numberDetection.isNumber(emailOrMoblie)){
                if (emailOrMoblie.length() != 11 || !emailOrMoblie.substring(0, 2).equals("09")) {
                    _emailOrMoblieText.setError("شماره موبایل نامعتبر است");
                    valid = false;
                } else {
                    address = "mobile";
                    _emailOrMoblieText.setError(null);
                }
            }
            else{
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrMoblie).matches()){
                    _emailOrMoblieText.setError("آدرس ایمیل نامعتبر است");
                    valid = false;
                }
                else {
                    address = "email";
                    _emailOrMoblieText.setError(null);
                }
            }
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("رمز عبور باید حداقل ۶ حرف باشد");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void sendLogin(){
        String emailORMoblie = _emailOrMoblieText.getText().toString();
        String password = _passwordText.getText().toString();
        String url = "";

        try {
            url = "http://" + Server.IP + ":" + Server.PORT + "/login.do?" +
                    address + "=" + emailORMoblie +
                    "&password=" + md5.getMD5(password);
        }catch (Exception e){
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("0")) {
                    Toast.makeText(getBaseContext(), "نام کاربری و یا رمزعبور شما اشتباه است", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    _loginButton.setEnabled(true);
                }
                else {
                    createUser(s);
                    progressDialog.dismiss();
                    onLoginSuccess();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"اختلال در اتصال",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                _loginButton.setEnabled(true);
            }
        });
        requestQueue.add(stringRequest);
    }

//    public class Login extends AsyncTask{
//
//        String emailORMoblie = _emailOrMoblieText.getText().toString();
//        String password = _passwordText.getText().toString();
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                URL url=new URL("http://"+ Server.IP+":"+Server.PORT+"/login.do?" +
//                        address+"="+emailORMoblie+
//                        "&password="+ md5.getMD5(password));
//
//                URLConnection urlConnection = url.openConnection();
//                InputStream inputStream = urlConnection.getInputStream();
//                int ascii = inputStream.read();
//                String content="";
//                while(ascii!= -1){
//                    content += (char)ascii;
//                    ascii=inputStream.read();
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
//                if (content.equals("0")) {
//                    Toast.makeText(getBaseContext(), "نام کاربری و یا رمزعبور شما اشتباه است", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    _loginButton.setEnabled(true);
//                }
//                else {
//                    createUser(content);
//                    progressDialog.dismiss();
//                    onLoginSuccess();
//                }
//            }
//            else{
//                Toast.makeText(getApplicationContext(),"اختلال در اتصال",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                _loginButton.setEnabled(true);
//            }
//        }
//    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    public void createUser(String content) {

        try{
            JSONParser jsonParser = new JSONParser();
            String decodeContent = "";
            try{
                AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123" , "arsham");
                AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(content);
                decodeContent = AesCbcWithIntegrity.decryptString(value,key);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONArray jsonArray = (JSONArray) jsonParser.parse(decodeContent);

            for(int i = 0 ; i < jsonArray.size() ; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String firstName = jsonObject.get("firstName").toString();
                String lastName = jsonObject.get("lastName").toString();
                String phoneNumber = jsonObject.get("phoneNumber").toString();
                String salesmanOrNot = jsonObject.get("salesmanOrNot").toString();
                String mailAddress = jsonObject.get("mailAddress").toString();
                String gender = jsonObject.get("gender").toString();
                String customerID = jsonObject.get("customerID").toString();
                String storeID = jsonObject.get("parlorID").toString();

                database = openOrCreateDatabase("Charsoo", MODE_PRIVATE, null);
                database.beginTransaction();
                database.execSQL("INSERT INTO Users (customerID,phoneNumber,lastName,firstName,mailAddress,gender,salesmanOrNot,storeID) VALUES (?,?,?,?,?,?,?,?)" , new String[]{customerID,"0"+phoneNumber,lastName,firstName,mailAddress,gender,salesmanOrNot,storeID});
                database.setTransactionSuccessful();
                database.endTransaction();
                database.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
