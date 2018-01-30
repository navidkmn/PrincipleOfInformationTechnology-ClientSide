package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.navid.androidproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class VerificationActivity extends AppCompatActivity {

    EditText _code;
    Button _save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        _code = (EditText) findViewById(R.id.code);

        _save = (Button) findViewById(R.id.save);
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_code.getText().toString().isEmpty())
                    _code.setError("لطفا کد را وارد کنید");
                else {
                    _code.setError(null);
                    Verification verification = new Verification();
                    verification.execute();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // DO NOTHING
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void verificationSuccess(){
        Intent intent = new Intent(getApplicationContext(), AppIntroductionActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public class Verification extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                URL url = new URL("" + _code.getText().toString());
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                int ascii = inputStream.read();
                String content="";
                while(ascii!= -1){
                    content += (char)ascii;
                    ascii=inputStream.read();
                }
                inputStream.close();
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            String content = o.toString();
            if(o.equals("1")){
                verificationSuccess();
            }
            if(o.equals("0"))
                Toast.makeText(getApplicationContext(),"کد وارد شده صحیح نمی‌باشد",Toast.LENGTH_SHORT).show();
        }
    }
}
