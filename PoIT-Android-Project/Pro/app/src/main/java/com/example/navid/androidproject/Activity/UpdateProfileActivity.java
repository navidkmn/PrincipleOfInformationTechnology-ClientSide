package com.example.navid.androidproject.Activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import com.example.navid.androidproject.Other.IdentityCardCorrection;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.AndroidEncoderUtil.MD5;
import com.example.navid.androidproject.Other.MyCalendar;
import com.example.navid.androidproject.Other.PersianDetection;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int REQUEST_AppIntroduction = 0;

    private InternetConnection internetConnection;
    private PersianDetection persianDetection;
    private IdentityCardCorrection identityCardCorrection;
    private MD5 md5;

    ProgressDialog progressDialog;

    private ImageView _back;
    private EditText _nameText;
    private EditText _familyText;
    private TextView _emailText;
    private EditText _mobileText;
    private EditText _password;
    private EditText _reEnterPassword;
    private CheckBox _confirm;
    private Button _signupButton;
    private TextInputLayout pass;
    private TextInputLayout rePass;

    private String newPassword = "";

    RequestQueue requestQueue;

    String firstName,lastName,email,phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
          /*
        FarsiSupport
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        internetConnection = new InternetConnection(this);
        persianDetection = new PersianDetection();
        md5 = new MD5();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        _back = (ImageView) findViewById(R.id.backUpdate);
        _back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _nameText = (EditText) findViewById(R.id.input_name);
        _nameText.setText(User.FIRST_NAME);
        _nameText.requestFocus();
        _familyText = (EditText) findViewById(R.id.input_family);
        _familyText.setText(User.LAST_NAME);
        _emailText = (TextView) findViewById(R.id.input_email);
        _emailText.setText(User.MAIL_ADDRESS);
        _mobileText = (EditText) findViewById(R.id.input_mobile);
        _mobileText.setText(User.PHONE_NUMBER);

        _confirm = (CheckBox) findViewById(R.id.changePass);
        _confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_confirm.isChecked()){
                    pass.setVisibility(View.VISIBLE);
                    rePass.setVisibility(View.VISIBLE);
                }
                else{
                    pass.setVisibility(View.INVISIBLE);
                    rePass.setVisibility(View.INVISIBLE);
                }
            }
        });

        _password = (EditText) findViewById(R.id.input_password);
        pass = (TextInputLayout) findViewById(R.id.tilPassword);
        _reEnterPassword = (EditText) findViewById(R.id.input_reEnterPassword);
        rePass = (TextInputLayout) findViewById(R.id.tilRepassword);

        pass.setVisibility(View.INVISIBLE);
        rePass.setVisibility(View.INVISIBLE);

        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnection.haveNetworkConnection()) {
                    signup();
                } else {
                        Toast.makeText(getApplicationContext() , "عدم اتصال اینترنت",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(UpdateProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("در حال تغییر اطلاعات کاربری ... لطفا صبر کنید");
        progressDialog.show();

        updateProfile();
//        UpdateProfile updateProfile = new UpdateProfile();
//        updateProfile.execute();
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent intent = new Intent();
        setResult(RESULT_OK , intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "عملیات ناموفق ، دوباره تلاش کنید", Toast.LENGTH_SHORT).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {

        boolean valid = true;

        String name = _nameText.getText().toString();
        String familyName = _familyText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _password.getText().toString();
        String reEnterPassword = _reEnterPassword.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError("لطفا نام خود را وارد کنید");
            valid = false;
        } else {
            if (!persianDetection.textIsPersian(name)) {
                _nameText.setError("لطفا نام خود را فارسی وارد کنید");
                valid = false;
            } else {
                _nameText.setError(null);
            }
        }

        if (familyName.isEmpty()) {
            _familyText.setError("لطفا نام خانوادگی خود را وارد کنید");
            valid = false;
        } else {
            if (!persianDetection.textIsPersian(familyName)) {
                _familyText.setError("لطفا نام خانوادگی خود را فارسی وارد کنید");
                valid = false;
            } else {
                _familyText.setError(null);
            }
        }

        if (mobile.isEmpty()) {
            _mobileText.setError("لطفا شماره موبایل خود را وارد کنید");
            valid = false;
        } else {
            if (mobile.length() != 11 || !mobile.substring(0, 2).equals("09")) {
                _mobileText.setError("شماره موبایل نامعتبر است");
                valid = false;
            } else {
                _mobileText.setError(null);
            }
        }

        if(_confirm.isChecked()) {

            if (password.isEmpty() && reEnterPassword.isEmpty()) {
                _password.setError(null);
                _reEnterPassword.setError(null);
            } else {
                if (password.isEmpty()) {
                    _password.setError("لطفا رمز عبور خود را وارد کنید ");
                    valid = false;
                } else {
                    if (_password.length() < 6) {
                        _password.setError("رمز عبور باید حداقل ۶ حرف باشد");
                        valid = false;
                    } else {
                        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
                            _reEnterPassword.setError("رمز عبور مطابقت ندارد");
                            valid = false;
                        } else {
                            _reEnterPassword.setError(null);
                            newPassword = password;
                        }
                    }
                }
            }
        }
        return valid;
    }

    public void updateProfile(){
        firstName = _nameText.getText().toString();
        lastName = _familyText.getText().toString();
        email = _emailText.getText().toString();
        phoneNumber = _mobileText.getText().toString();
        String url = "";
        try {
            url = "http://" + Server.IP + ":" + Server.PORT + "/updateProfile.do?" +
                    "firstname=" + firstName +
                    "&lastname=" + lastName +
                    "&email=" + email +
                    "&phone=" + phoneNumber +
                    "&password=" + md5.getMD5(newPassword);
        }catch (Exception e){
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("0")) {
                    _signupButton.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();

                    User.FIRST_NAME = firstName;
                    User.LAST_NAME = lastName;
                    User.PHONE_NUMBER = phoneNumber;

                    Toast.makeText(getApplicationContext(),"تغییر حساب کاربری با موفقیت انجام شد",Toast.LENGTH_SHORT).show();
                    SQLiteDatabase database = openOrCreateDatabase("Charsoo",MODE_PRIVATE,null);
                    database.beginTransaction();
                    database.execSQL("UPDATE USERS SET firstName = ? WHERE mailAddress = ?" , new String[]{User.FIRST_NAME,User.MAIL_ADDRESS});
                    database.execSQL("UPDATE USERS SET lastName = ? WHERE mailAddress = ?" , new String[]{User.LAST_NAME,User.MAIL_ADDRESS});
                    database.execSQL("UPDATE USERS SET phoneNumber = ? WHERE mailAddress = ?" , new String[]{User.PHONE_NUMBER,User.MAIL_ADDRESS});
                    database.setTransactionSuccessful();
                    database.endTransaction();
                    database.close();
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"اختلال در ارتباط با سرور",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                _signupButton.setEnabled(true);
            }
        });
        requestQueue.add(stringRequest);
    }

//    public class UpdateProfile extends AsyncTask {
//
//        String firstName = _nameText.getText().toString();
//        String lastName = _familyText.getText().toString();
//        String email = _emailText.getText().toString();
//        String phoneNumber = _mobileText.getText().toString();
////        MyCalendar myCalendar = new MyCalendar();
////        String registrationDate = Long.toString(myCalendar.getMilliSecondTime());
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/updateProfile.do?" +
//                        "firstname=" + firstName +
//                        "&lastname=" + lastName +
//                        "&email=" + email +
//                        "&phone=" + phoneNumber +
//                        "&password=" + md5.getMD5(newPassword));
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
//                    // Update User Info in User class
//                    if(o.toString().equals("1")){
//                        progressDialog.dismiss();
//
//                        User.FIRST_NAME = firstName;
//                        User.LAST_NAME = lastName;
//                        User.PHONE_NUMBER = phoneNumber;
//
//                        Toast.makeText(getApplicationContext(),"تغییر حساب کاربری با موفقیت انجام شد",Toast.LENGTH_SHORT).show();
//                        SQLiteDatabase database = openOrCreateDatabase("Charsoo",MODE_PRIVATE,null);
//                        database.beginTransaction();
//                        database.execSQL("UPDATE USERS SET firstName = ? WHERE mailAddress = ?" , new String[]{User.FIRST_NAME,User.MAIL_ADDRESS});
//                        database.execSQL("UPDATE USERS SET lastName = ? WHERE mailAddress = ?" , new String[]{User.LAST_NAME,User.MAIL_ADDRESS});
//                        database.execSQL("UPDATE USERS SET phoneNumber = ? WHERE mailAddress = ?" , new String[]{User.PHONE_NUMBER,User.MAIL_ADDRESS});
//                        database.setTransactionSuccessful();
//                        database.endTransaction();
//                        database.close();
//                        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    }
//                    else{
//                        _signupButton.setEnabled(true);
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//
//                }
//            }
//    } public class UpdateProfile extends AsyncTask {
//
//        String firstName = _nameText.getText().toString();
//        String lastName = _familyText.getText().toString();
//        String email = _emailText.getText().toString();
//        String phoneNumber = _mobileText.getText().toString();
////        MyCalendar myCalendar = new MyCalendar();
////        String registrationDate = Long.toString(myCalendar.getMilliSecondTime());
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/updateProfile.do?" +
//                        "firstname=" + firstName +
//                        "&lastname=" + lastName +
//                        "&email=" + email +
//                        "&phone=" + phoneNumber +
//                        "&password=" + md5.getMD5(newPassword));
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
//                    // Update User Info in User class
//                    if(o.toString().equals("1")){
//                        progressDialog.dismiss();
//
//                        User.FIRST_NAME = firstName;
//                        User.LAST_NAME = lastName;
//                        User.PHONE_NUMBER = phoneNumber;
//
//                        Toast.makeText(getApplicationContext(),"تغییر حساب کاربری با موفقیت انجام شد",Toast.LENGTH_SHORT).show();
//                        SQLiteDatabase database = openOrCreateDatabase("Charsoo",MODE_PRIVATE,null);
//                        database.beginTransaction();
//                        database.execSQL("UPDATE USERS SET firstName = ? WHERE mailAddress = ?" , new String[]{User.FIRST_NAME,User.MAIL_ADDRESS});
//                        database.execSQL("UPDATE USERS SET lastName = ? WHERE mailAddress = ?" , new String[]{User.LAST_NAME,User.MAIL_ADDRESS});
//                        database.execSQL("UPDATE USERS SET phoneNumber = ? WHERE mailAddress = ?" , new String[]{User.PHONE_NUMBER,User.MAIL_ADDRESS});
//                        database.setTransactionSuccessful();
//                        database.endTransaction();
//                        database.close();
//                        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    }
//                    else{
//                        _signupButton.setEnabled(true);
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//
//                }
//            }
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}