package com.example.navid.androidproject.Activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import roboguice.util.temp.Ln;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int REQUEST_AppIntroduction = 0;

    private InternetConnection internetConnection;
    private PersianDetection persianDetection;
    private IdentityCardCorrection identityCardCorrection;
    private MD5 md5;

    ProgressDialog progressDialog;

    private EditText _nameText;
    private EditText _familyText;
    private TextView _sexTextView;
    private RadioButton _maleRadioButton;
    private RadioButton _femaleRadioButton;
    private EditText _identityCardText;
    private EditText _emailText;
    private EditText _mobileText;
    private EditText _passwordText;
    private EditText _reEnterPasswordText;
    private Button _signupButton;
    private TextView _loginLink;

    String firstName,lastName,sexID,mailAddress,phoneNumber,password,nationalID,registrationDate;

    RequestQueue requestQueue;

    String[] personalInformation;
    String registrationURL;
    /////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
          /*
        FarsiSupport
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        internetConnection = new InternetConnection(this);
        persianDetection = new PersianDetection();
        identityCardCorrection = new IdentityCardCorrection();
        md5 = new MD5();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());
//
//        Typeface font = Typeface.createFromAsset(getApplication().getAssets() , "font/A MehdiHeydari.ttf");

        _nameText = (EditText) findViewById(R.id.input_name);
        _nameText.requestFocus();
        _familyText = (EditText) findViewById(R.id.input_family);
        _sexTextView = (TextView) findViewById(R.id.textView_sex);
        _identityCardText = (EditText) findViewById(R.id.indentity_card);
        _emailText = (EditText) findViewById(R.id.input_email);
        _mobileText = (EditText) findViewById(R.id.input_mobile);

        _passwordText = (EditText) findViewById(R.id.input_password);
        //  _passwordText.setTypeface(font);
        _passwordText.setTransformationMethod(new PasswordTransformationMethod());

        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        // _reEnterPasswordText.setTypeface(font);
        _reEnterPasswordText.setTransformationMethod(new PasswordTransformationMethod());

        _maleRadioButton = (RadioButton) findViewById(R.id.radioButton_male);
        _maleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _sexTextView.setError(null);
            }
        });

        _femaleRadioButton = (RadioButton) findViewById(R.id.radioButtonـ_female);
        _femaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _sexTextView.setError(null);
            }
        });

        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnection.haveNetworkConnection()) {
                    signup();
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                    intent.putExtra("activity", 8);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        _loginLink = (TextView) findViewById(R.id.link_login);
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        //Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("ایجاد حساب ... لطفا صبر کنید");
        progressDialog.show();

        register();
//        Register register = new Register();
//        register.execute();
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        //setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), AppIntroductionActivity.class); //replace with verification activity
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "ورود ناموفق", Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {

        boolean valid = true;

        String name = _nameText.getText().toString();
        String familyName = _familyText.getText().toString();
        String sexID = (_maleRadioButton.isChecked() ? "1" : "0");
        String identityCard = _identityCardText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

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

        if (!_maleRadioButton.isChecked() && !_femaleRadioButton.isChecked()) {
            _sexTextView.setError("لطفا جنسیت خود را مشخص کنید");
            valid = false;
        } else {
            _sexTextView.setError(null);
        }

        if (identityCard.isEmpty()) {
            _identityCardText.setError("لطغا کدملی خود را وارد کنید");
            valid = false;
        } else {
            if (!identityCardCorrection.isIdentityCard(identityCard)) {
                _identityCardText.setError("کدملی نامعتبر است");
                valid = false;
            } else
                _identityCardText.setError(null);
        }

        if (email.isEmpty()) {
            _emailText.setError("لطفا آدرس ایمیل خود را وارد کنید");
            valid = false;
        } else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _emailText.setError("آدرس ایمیل نامعتبر است");
                valid = false;
            } else {
                _emailText.setError(null);
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

        if (password.isEmpty()) {
            _passwordText.setError("لطفا رمز عبور خود را وارد کنید ");
            valid = false;
        } else {
            if (_passwordText.length() < 6) {
                _passwordText.setError("رمز عبور باید حداقل ۶ حرف باشد");
                valid = false;
            } else {
                _passwordText.setError(null);
            }
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("رمز عبور مطابقت ندارد");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }
        return valid;
    }

    private void register() {

        firstName = _nameText.getText().toString();
        lastName = _familyText.getText().toString();
        sexID = (_maleRadioButton.isChecked() ? "M" : "F");
        mailAddress = _emailText.getText().toString();
        phoneNumber = _mobileText.getText().toString();
        password = _passwordText.getText().toString();
        nationalID = _identityCardText.getText().toString();
        registrationDate = Long.toString(System.currentTimeMillis());
        String url = "";
        try {
            url = "http://" + Server.IP + ":" + Server.PORT + "/register.do?" +
                    "firstName=" + firstName +
                    "&lastName=" + lastName +
                    "&nationalID=" + nationalID +
                    "&gender=" + sexID +
                    "&registeredDate=" + registrationDate +
                    "&mailAddress=" + mailAddress +
                    "&phoneNumber=" + phoneNumber +
                    "&passCode=" + md5.getMD5(password);
        }catch (Exception e){
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("0")) {
                    Toast.makeText(getBaseContext(), "شما قبلا ثبت‌نام کرده‌اید", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    goToLogin();
                } else {
                    String customerID = s;
                    createUser(new String[]{customerID,phoneNumber,lastName,firstName,mailAddress,sexID,"0","0"});
                    //sendNotification(content);
                    progressDialog.dismiss();
                    onSignupSuccess();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onSignupFailed();
                progressDialog.dismiss();
            }
        });
        requestQueue.add(stringRequest);
    }

//    public class Register extends AsyncTask {
//
//        String firstName = _nameText.getText().toString();
//        String lastName = _familyText.getText().toString();
//        String sexID = (_maleRadioButton.isChecked() ? "M" : "F");
//        String mailAddress = _emailText.getText().toString();
//        String phoneNumber = _mobileText.getText().toString();
//        String password = _passwordText.getText().toString();
//        String nationalID = _identityCardText.getText().toString();
//        String registrationDate = Long.toString(System.currentTimeMillis());
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/register.do?" +
//                        "firstName=" + firstName +
//                        "&lastName=" + lastName +
//                        "&nationalID=" + nationalID +
//                        "&gender=" + sexID +
//                        "&registeredDate=" + registrationDate +
//                        "&mailAddress=" + mailAddress +
//                        "&phoneNumber=" + phoneNumber +
//                        "&passCode=" + md5.getMD5(password));
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
//
//                if (content.equals("0")) {
//                    Toast.makeText(getBaseContext(), "شما قبلا ثبت‌نام کرده‌اید", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                    goToLogin();
//                } else {
//                    String customerID = content;
//                    createUser(new String[]{customerID,phoneNumber,lastName,firstName,mailAddress,sexID,"0","0"});
//                    //sendNotification(content);
//                    progressDialog.dismiss();
//                    onSignupSuccess();
//                }
//            }
//            else{
//                    onSignupFailed();
//                    //Toast.makeText(getApplicationContext(),"اختلال در اتصال",Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//            }
//        }
//    }

        public void createUser(String[] userInfo){
            SQLiteDatabase database = openOrCreateDatabase("Charsoo", MODE_PRIVATE, null);
            database.beginTransaction();
            database.execSQL("INSERT INTO Users (customerID,phoneNumber,lastName,firstName,mailAddress,gender,salesmanOrNot,storeID) VALUES (?,?,?,?,?,?,?,?)" ,userInfo);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        }

        public void goToLogin() {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        @Override
        public void onBackPressed() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

        public void sendNotification(String code) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.shop).setContentTitle("نوید گستر").setContentText(code);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(001, mBuilder.build());
        }
    }