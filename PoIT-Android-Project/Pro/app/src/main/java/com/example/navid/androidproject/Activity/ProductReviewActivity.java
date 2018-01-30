package com.example.navid.androidproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.navid.androidproject.Adapter.CommentAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.Fragment.MyDialogFragment;
import com.example.navid.androidproject.Listener.EndlessRecyclerOnScrollListener;
import com.example.navid.androidproject.Other.Comment;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StaticInfo;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.example.navid.androidproject.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ProductReviewActivity extends AppCompatActivity {

    ImageView _backArrow;
    FloatingActionButton _addComment;

    CommentAdapter commentAdapter;
    RecyclerView recyclerView;

    ArrayList<Comment> comments;
    ArrayList<Comment> rvCommentList;
    private ProgressBar progressBar;

    MyDialogFragment myDialogFragment;

    int pre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_review);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        pre = getIntent().getIntExtra("pre" , -1);

        progressBar = (ProgressBar) findViewById(R.id.item_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(R.color.primary, PorterDuff.Mode.SRC_IN);

        comments = new ArrayList<>();
        rvCommentList = new ArrayList<>();

        GetComments getComments = new GetComments();
        getComments.execute();

        _backArrow = (ImageView) findViewById(R.id.backFromReview);
        _backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 //               int prepre = getIntent().getIntExtra("prepre" , -1);
 //               if(prepre == 1) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK , intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                }
//                if(prepre == 2){
//                    Intent intent = new Intent();
//                    setResult(RESULT_OK , intent);
//                    finish();
//                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                }
            }
        });

        _addComment = (FloatingActionButton) findViewById(R.id.addComment);
        _addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!User.LOGIN_STATUS){
                    myDialogFragment = MyDialogFragment.newInctance("MainActivity","لطفا به حساب کاربری خود وارد شوید" , "بستن" , R.drawable.warning2);
                    myDialogFragment.show(getFragmentManager(),"dialog");
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);
                    //finish();
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
    }

    public void doNext(){

        recyclerView = (RecyclerView) findViewById(R.id.comments);
        //recyclerView.addItemDecoration(new DividerItemDecoration(ProductReviewActivity.this,DividerItemDecoration.VERTICAL));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL , true));
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        commentAdapter = new CommentAdapter(getApplicationContext() , rvCommentList);
        recyclerView.setAdapter(commentAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int last = rvCommentList.size();
                        int end = last + 10;
                        for(int i = last; i<end ; i++) {
                            if (i < comments.size()) {
                                rvCommentList.add(comments.get(i));
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
    //    int prepre = getIntent().getIntExtra("prepre" , -1);
        //if(prepre == 1) {
            Intent intent = new Intent();
            setResult(RESULT_OK , intent);
            finish();
           // startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        //}
        //if(prepre == 2){
//            Intent intent = new Intent();
//            setResult(RESULT_OK , intent);
//            finish();
//            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        //}
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == RESULT_OK) {
                comments = new ArrayList<>();
                rvCommentList = new ArrayList<>();
                GetComments getComments = new GetComments();
                getComments.execute();
                Log.e("Barcelona" , "Realmadrid");
            }
        }
    }

    public void getCommentsFromServer(String input){
        try{
            JSONParser jsonParser = new JSONParser();
            String decodeContent = "";
            try{
                AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword("navid123" , "arsham");
                AesCbcWithIntegrity.CipherTextIvMac value = new AesCbcWithIntegrity.CipherTextIvMac(input);
                decodeContent = AesCbcWithIntegrity.decryptString(value,key);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONArray jsonArray = (JSONArray) jsonParser.parse(decodeContent);

            for(int i = 0 ; i < jsonArray.size() ; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String description = jsonObject.get("text").toString().replace('_',' ');
                String priceRate = jsonObject.get("ES").toString();
                String qualityRate = jsonObject.get("QS").toString();
                String submitDate = jsonObject.get("submitDate").toString();
                String senderFirstname = jsonObject.get("name").toString();
                String senderLastname = jsonObject.get("surname").toString();
                comments.add(new Comment(senderFirstname + " "+senderLastname , description , submitDate , priceRate , qualityRate));
            }

            for(int i = 0 ; i < 10 ; i++)
                if(i < comments.size())
                    rvCommentList.add(comments.get(i));

            progressBar.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetComments extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/getReviews.do?" +
                        "productID="+ StaticInfo.SAVED_PRODUCT.getID());

                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                int ascii = inputStream.read();
                String content = "";
                while (ascii != -1) {
                    content += (char) ascii;
                    ascii = inputStream.read();
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

            if (o != null) {
                if(o.toString().equals("0")){
                    Toast.makeText(getApplicationContext(),"موجود نیست", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    getCommentsFromServer(o.toString());
                    doNext();
                }
            } else {
                Toast.makeText(getApplicationContext(),"نظری موجود نیست", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
