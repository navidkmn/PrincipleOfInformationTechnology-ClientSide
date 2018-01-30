package com.example.navid.androidproject.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navid.androidproject.R;
import com.soundcloud.android.crop.Crop;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import id.zelory.compressor.Compressor;
import com.example.navid.androidproject.Adapter.ProductCreationAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.AesCbcWithIntegrity;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Other.CategoryEnum;
import com.example.navid.androidproject.Other.MyCalendar;
import com.example.navid.androidproject.Other.PersianDetection;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.crypto.KeyGenerator;
import javax.net.ssl.HttpsURLConnection;

public class ProductCreationActivity extends AppCompatActivity {

    static final int IMG_REQUEST = 1;
    static final int IMG_LIMIT = 5;

    static final int RETRY = 5;
    public int imageSedingTry;

    RequestQueue rq,requestQueue;

    public EditText _productName,_productPrice,_productQuantity;
    public EditText _description;

    public Button _save;
    public Button _cancel;
    public FloatingActionButton floatingActionButton;
    public Spinner categorySpinner,subCategorySpinner;

    public ProductCreationAdapter productCreationAdapter;
    public RecyclerView productImageRV;
    public ArrayList<Uri> productImages;

    String productName,productPrice;

    public PersianDetection persianDetection;
    ProgressDialog progressDialog;

    String productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_creation);

        productID = "";

        imageSedingTry = 0;
        
        persianDetection = new PersianDetection();

        rq = Volley.newRequestQueue(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        _productName = (EditText) findViewById(R.id.product);
        _productPrice = (EditText) findViewById(R.id.price);
        _productQuantity = (EditText) findViewById(R.id.quantity);
        _description = (EditText) findViewById(R.id.description);

        _save = (Button) findViewById(R.id.save);
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productID.equals("")) {
                    if (isOkay()) {
                        _save.setEnabled(false);
                        progressDialog = new ProgressDialog(ProductCreationActivity.this, R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("ساخت کالا ... لطفا صبر کنید");
                        progressDialog.show();
                        productCreation();
//                        ProductCreation productCreation = new ProductCreation();
//                        productCreation.execute();
                    }
                }
                else{
                    _save.setEnabled(false);
                    progressDialog = new ProgressDialog(ProductCreationActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("ارسال عکس ... لطفا صبر کنید");
                    progressDialog.show();
                    sendRequest();
                }
            }
        });

        _cancel = (Button)findViewById(R.id.cancel);
        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSubCategoryList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCategorySpinner = (Spinner) findViewById(R.id.subCategorySpinner);
        setSubCategoryList();

        productImages = new ArrayList<>();

        productImageRV = (RecyclerView) findViewById(R.id.productImagesRV);
        productCreationAdapter = new ProductCreationAdapter(getApplicationContext() , productImages , this);
        productImageRV.setAdapter(productCreationAdapter);
        productImageRV.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.HORIZONTAL ,true));
    }

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            if(s.equals("1")){
                progressDialog.dismiss();
                backToManagement(_productName.getText().toString(),_productPrice.getText().toString(),productImages.get(0));
            }
            else{
                if(imageSedingTry < RETRY){
                    sendRequest();
                    imageSedingTry++;
                }
                else {
                    _save.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "عکس‌های کالا ثبت نشد. بار دیگر تلاش کنید", Toast.LENGTH_SHORT).show();
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

    public void productCreation(){
        String submitDate =  Long.toString(System.currentTimeMillis());
        String productName = _productName.getText().toString();
        String productPrice = _productPrice.getText().toString();
        String productQuantity = _productQuantity.getText().toString();
        String description  = _description.getText().toString();
        String sendDescription = description.replace(' ','_');

        String url = "http://" + Server.IP + ":" + Server.PORT + "/insertProduct.do?" +
                "name="+ productName +
                "&subcategoryID="+ findSubCategory() +
                "&description="+ sendDescription +
                "&price=" + productPrice +
                "&offPrice=0" +
                "&quantity=" + productQuantity +
                "&submitDate="+ submitDate +
                "&parlorID="+ User.STORE_ID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("0")) {
                    _save.setEnabled(true);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                }
                else {
                    productID = s;
                    sendRequest();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                _save.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"عدم ارتباط با سرور",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void sendRequest(){

        String url = "http://" + Server.IP + ":" + Server.PORT + "/iinsertProduct.do";

        try {
            final String[] images = new String[5];
            for(int i = 0 ; i < productImages.size() ; i++){
                try {
                    InputStream UriInputStream = getContentResolver().openInputStream(productImages.get(i));
                    String poster = BankBase64.encodeToString(IOUtils.toByteArray(UriInputStream), 0);
                    images[i] = poster;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            StringRequest jor = new StringRequest(Request.Method.POST , url , responseListener , errorListener){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> param = new HashMap<String, String>();

                    JSONObject jo = new JSONObject();

                    jo.put("productID" , productID);

                    for (int i = 0; i < productImages.size(); i++) {
                        jo.put("image" + Integer.toString(i + 1), images[i]);
                    }

                    param.put("JSON", jo.toJSONString());

                    return param;
                }
            };
            rq.add(jor);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext() , "error" , Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOkay(){
        boolean isOkay = true;

        if (_productName.getText().toString().isEmpty()) {
            _productName.setError("لطفا نام کالای خود را وارد کنید");
            isOkay = false;
        } else {
            if (!persianDetection.textIsPersian(_productName.getText().toString())) {
                _productName.setError("لطفا نام کالای خود را فارسی وارد کنید");
                isOkay = false;
            } else {
                _productName.setError(null);
            }
        }

        if (_productPrice.getText().toString().isEmpty()) {
            _productPrice.setError("لطفا قیمت کالای خود را وارد کنید");
            isOkay = false;
        } else {
            _productPrice.setError(null);
        }

        if (_productQuantity.getText().toString().isEmpty()) {
            _productQuantity.setError("لطفا تعداد کالای خود را وارد کنید");
            isOkay = false;
        } else {
            _productQuantity.setError(null);
        }

        if (_description.getText().toString().isEmpty()) {
            _description.setError("لطفا اطلاعات کالای خود را وارد کنید");
            isOkay = false;
        } else {
            if (!persianDetection.textIsPersian(_description.getText().toString())) {
                _description.setError("لطفا اطلاعات کالای خود را فارسی وارد کنید");
                isOkay = false;
            } else {
                _description.setError(null);
            }
        }

        if(productImages.isEmpty()){
            isOkay = false;
            Toast.makeText(getApplicationContext(),"عکس کالای را وارد کنید",Toast.LENGTH_SHORT).show();
        }

        return isOkay;
    }

    public void pickImageFromGallery(){
        Crop.pickImage(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
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
            if(productImages.size() != IMG_LIMIT) {
                Uri image = Crop.getOutput(result);
                File file = new File(image.getPath());
                try {
                    File resizedFile = new Compressor(this).compressToFile(file);
                    productImages.add(Uri.fromFile(resizedFile));
                    productCreationAdapter.notifyItemInserted(productImages.size() - 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"شما می‌توانید حداکثر ۵ عکس انتخاب کنید",Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setSubCategoryList(){
        List<String> list = new ArrayList<String>();
        int categoryID = categorySpinner.getSelectedItemPosition() + 1;
        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(CategoryEnum.relation);
            JSONArray jsonArray = (JSONArray) jsonObject.get(Integer.toString(categoryID));
            for(int i = 0 ; i < jsonArray.size() ; i++)
                list.add(CategoryEnum.SubCategory[Integer.parseInt(jsonArray.get(i).toString())]);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subCategorySpinner.setAdapter(dataAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void backToManagement(String productName,String productPrice,Uri image){

        String[] productInfo = new String[10];
        productInfo[0] = "0";
        productInfo[1] = productName;
        productInfo[2] = StringArrayList.getPriceFormat(productPrice);
        productInfo[3] = "0";
        productInfo[4] = "0";
        productInfo[5] = "0";
        productInfo[6] = image.getPath();
        productInfo[7] = "0";
        productInfo[8] = "0";
        productInfo[9] = "0";
        Intent intent = new Intent();
        intent.putExtra("productInfo",productInfo);
        setResult(RESULT_OK,intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void deleteProductImage(int position){
        productImages.remove(position);
		((ViewGroup)productImageRV.getChildAt(position).getParent()).removeView(productImageRV.getChildAt(position));
        productCreationAdapter.notifyItemRemoved(position);
		//productCreationAdapter.notifyItemChanged(position);
    }

//    public String getImageString(){
//        String result = "";
//        for(int i = 0 ; i < productImages.size() ;i++) {
//            try {
//                InputStream UriInputStream = getContentResolver().openInputStream(productImages.get(i));
//                String poster = BankBase64.encodeToString(IOUtils.toByteArray(UriInputStream), 0);
//                String temp = "&image" + Integer.toString(i+1) + "=" + poster; //replace with poster
//                result += temp;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        // no image
//        for(int i = productImages.size() ; i < IMG_LIMIT ;i++){
//            String temp = "&image" + Integer.toString(i+1) + "=" + ""; //repalce with "" (empty)
//            result += temp;
//        }
//
//        return result;
//    }

    public String findSubCategory(){
        String subCategory = subCategorySpinner.getSelectedItem().toString();
        for(int i = 0 ; i < CategoryEnum.SubCategory.length ; i++)
            if(CategoryEnum.SubCategory[i].equals(subCategory))
                return Integer.toString(i);
        return "0";
    }

//    public class ProductCreation extends AsyncTask {
//
//        String submitDate =  Long.toString(System.currentTimeMillis());
//        String productName = _productName.getText().toString();
//        String productPrice = _productPrice.getText().toString();
//        String productQuantity = _productQuantity.getText().toString();
//        String description  = _description.getText().toString();
//        String sendDescription = description.replace(' ','_');
//
//        @Override
//        protected Object doInBackground(Object[] params) {
//            try {
//                URL url = new URL("http://" + Server.IP + ":" + Server.PORT + "/insertProduct.do?" +
//                        "name="+ productName +
//                        "&subcategoryID="+ findSubCategory() +
//                        "&description="+ sendDescription +
//                        "&price=" + productPrice +
//                        "&offPrice=0" +
//                        "&quantity=" + productQuantity +
//                        "&submitDate="+ submitDate +
//                        "&parlorID="+ User.STORE_ID);
//
//                URLConnection urlConnection = url.openConnection();
//                urlConnection.connect();
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
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Log.e("Navid" , content);
//                    productID = content;
//                    sendRequest();
//                }
//            } else {
//                _save.setEnabled(true);
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(),"عدم ارتباط با سرور",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
