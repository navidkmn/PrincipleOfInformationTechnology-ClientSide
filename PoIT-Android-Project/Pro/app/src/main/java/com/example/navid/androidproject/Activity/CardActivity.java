package com.example.navid.androidproject.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navid.androidproject.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.ButterKnife;
import com.example.navid.androidproject.Adapter.CartAdapter;
import com.example.navid.androidproject.AndroidEncoderUtil.BankBase64;
import com.example.navid.androidproject.Fragment.MyDialogFragment;
import com.example.navid.androidproject.Other.CartList;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.MyCalendar;
import com.example.navid.androidproject.Other.Server;
import com.example.navid.androidproject.Other.ShoppingBagProduct;
import com.example.navid.androidproject.Other.StringArrayList;
import com.example.navid.androidproject.Other.StringGenerator;
import com.example.navid.androidproject.Other.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CardActivity extends AppCompatActivity {

    private static final int REQUEST_AppCard = 0;

    public ImageView _back;
    public RecyclerView productRV;
    public Button _saveButton;
    public RelativeLayout _saveCart;
    public TextView _emptyCart;
    public CartAdapter cartAdapter;
    public TextView sumPriceText;

    private RadioButton _cashRadioButton;
    private RadioButton _onlineRadioButton;

    public InternetConnection internetConnection;
    public SQLiteDatabase cart;

    public String addressStr;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        CartList.CART_PRODUCT_LIST = new ArrayList<>();

        internetConnection = new InternetConnection(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("font/A MehdiHeydari.ttf").setFontAttrId(R.attr.fontPath).build());

        sumPriceText = (TextView) findViewById(R.id.sumText);

        cart = openOrCreateDatabase("cart" , MODE_PRIVATE , null);
        cart.beginTransaction();
        Cursor cursor = cart.rawQuery("SELECT * FROM cartProducts",null);

        while (cursor.moveToNext()){
            try {
                File file = new File(cursor.getString(cursor.getColumnIndex("image")));
                Uri uri = Uri.fromFile(file);

                CartList.CART_PRODUCT_LIST.add
                        (new ShoppingBagProduct(
                                        cursor.getString(cursor.getColumnIndex("id")),
                                        cursor.getString(cursor.getColumnIndex("name")),
                                        cursor.getString(cursor.getColumnIndex("price")),
                                        uri,
                                        cursor.getString(cursor.getColumnIndex("storeName")),
                                        cursor.getString(cursor.getColumnIndex("quantity"))
                                )
                        );
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        cart.setTransactionSuccessful();
        cart.endTransaction();
        cart.close();

        calculateSumPrice();

        _saveCart = (RelativeLayout) findViewById(R.id.saveCartRL);
        _emptyCart = (TextView) findViewById(R.id.emptyCart);
        if(CartList.CART_PRODUCT_LIST.isEmpty()) {
            _saveCart.setVisibility(View.GONE);
            _emptyCart.setVisibility(View.VISIBLE);
        }
        else{
            _saveCart.setVisibility(View.VISIBLE);
            _emptyCart.setVisibility(View.INVISIBLE);
        }

        productRV = (RecyclerView) findViewById(R.id.productRV);
        cartAdapter = new CartAdapter(getApplicationContext() , CartList.CART_PRODUCT_LIST , this);
        productRV.setAdapter(cartAdapter);
        productRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL , true));

        _back = (ImageView) findViewById(R.id.backFromCard);
        _back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(internetConnection.haveNetworkConnection()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NoConnectionActivity.class);
                    intent.putExtra("activity" , 0);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        _saveButton = (Button) findViewById(R.id.saveButton);
        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!User.LOGIN_STATUS){
                    MyDialogFragment myDialogFragment = MyDialogFragment.newInctance("CartActivity","لطفا به حساب کاربری خود وارد شوید" , "بستن" , R.drawable.warning2);
                    myDialogFragment.show(getFragmentManager(),"dialog");
                }else {
                    final Dialog dialog = new Dialog(CardActivity.this);
                    dialog.setContentView(R.layout.address_dialog_cart);
                    dialog.setTitle("آدرس دقیق محل ارسال سبد خرید");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    Button _save = (Button) dialog.findViewById(R.id.saveAddress);
                    ImageView _cancel = (ImageView) dialog.findViewById(R.id.dismiss);

                    RadioButton _cashRadioButton = (RadioButton) dialog.findViewById(R.id.radioButton_cash);
                    _cashRadioButton.setEnabled(false);
                    _cashRadioButton.setChecked(true);

                    RadioButton _onlineRadioButton = (RadioButton) dialog.findViewById(R.id.radioButton_online);
                    _onlineRadioButton.setEnabled(false);
                    _onlineRadioButton.setChecked(false);

                    _save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText address = (EditText) dialog.findViewById(R.id.address);
                            addressStr = address.getText().toString();
                            if (addressStr.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "لطفا آدرس خود را وارد کنید", Toast.LENGTH_SHORT).show();
                            } else{
                                progressDialog = new ProgressDialog(CardActivity.this, R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("در حال ثبت سبد خرید ... لطفا صبر کنید");
                                progressDialog.setCanceledOnTouchOutside(false);
                                _saveButton.setEnabled(false);
                                progressDialog.show();

                                dialog.dismiss();
                                CartInfo cartInfo = new CartInfo();
                                cartInfo.execute();
                            }
                        }
                    });

                    _cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void deleteProduct(int position , String name , String store){

        cart = openOrCreateDatabase("cart" , MODE_PRIVATE , null);
        cart.beginTransaction();
        cart.execSQL("DELETE FROM cartProducts WHERE name = ? AND storeName = ?",new String[]{name,store});
        cart.setTransactionSuccessful();
        cart.endTransaction();
        cart.close();

        //((ViewGroup)productRV.getChildAt(position).getParent()).removeView(productRV.getChildAt(position));
        CartList.CART_PRODUCT_LIST.remove(position);

        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyDataSetChanged();
        cartAdapter.notifyItemChanged(position);

        if(CartList.CART_PRODUCT_LIST.isEmpty()) {
            _saveCart.setVisibility(View.GONE);
            _emptyCart.setVisibility(View.VISIBLE);
        }

        calculateSumPrice();
    }

    public void updateSQLite_Cart(int newQuantity , String name , String store){
        cart = openOrCreateDatabase("cart" , MODE_PRIVATE , null);
        cart.beginTransaction();
        cart.execSQL("UPDATE cartProducts SET quantity = ? WHERE name = ? AND storeName = ?" ,new String[]{Integer.toString(newQuantity), name ,store});
        cart.setTransactionSuccessful();
        cart.endTransaction();
        cart.close();

        for(int i = 0 ; i < CartList.CART_PRODUCT_LIST.size() ; i++){
            if(CartList.CART_PRODUCT_LIST.get(i).getProductName().equals(name) && CartList.CART_PRODUCT_LIST.get(i).getProductStore().equals(store))
                CartList.CART_PRODUCT_LIST.get(i).setProductQuantity(Integer.toString(newQuantity));
        }

        calculateSumPrice();
    }

    public void calculateSumPrice(){
        int sum = 0;
        for(int i = 0 ; i < CartList.CART_PRODUCT_LIST.size() ; i++){
            String price = StringArrayList.getPriceIntFormat(CartList.CART_PRODUCT_LIST.get(i).getProductPrice());
            sum += (Integer.parseInt(price) * Integer.parseInt(CartList.CART_PRODUCT_LIST.get(i).getProductQuantity()));
        }
        sumPriceText.setText(StringArrayList.getPriceFormat(Integer.toString(sum)));
    }

    public void deleteDB(){
        cart = openOrCreateDatabase("cart" , MODE_PRIVATE , null);
        cart.beginTransaction();
        cart.execSQL("DELETE FROM cartProducts");
        cart.setTransactionSuccessful();
        cart.endTransaction();
        cart.close();
    }

    public void doPositiveClick(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public String productStr(){
        String product = "";
        for(int i = 0 ; i < CartList.CART_PRODUCT_LIST.size() - 1 ; i++){
            String temp = CartList.CART_PRODUCT_LIST.get(i).getProductID()+","
                    +CartList.CART_PRODUCT_LIST.get(i).getProductQuantity()+"-";
            product += temp;
        }
        int lastProduct = CartList.CART_PRODUCT_LIST.size() - 1;
        String temp = CartList.CART_PRODUCT_LIST.get(lastProduct).getProductID()+","
                +CartList.CART_PRODUCT_LIST.get(lastProduct).getProductQuantity();
        product+= temp;
        return product;
    }

// SEND INFORMATION TO SERVER
    public class CartInfo extends AsyncTask{

        String purchaseDate = Long.toString(System.currentTimeMillis());
        String productStr = productStr();

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                URL url=new URL("http://"+ Server.IP+":"+Server.PORT+"/submitCart.do?" +
                        "customerID="+ User.CUSTOMER_ID+
                        "&submitDate="+ purchaseDate +
                        "&products=" + productStr +
                        "&address=" + addressStr);

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

            if (o != null) {
                String content = o.toString();
                if(content.equals("1")){
                    Toast.makeText(getApplicationContext(),"درخواست شما با موفقیت ثبت شد",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    deleteDB();
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"بار دیگر تلاش کنید",Toast.LENGTH_SHORT).show();
                    _saveButton.setEnabled(true);
                }
            }
            else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"اختلال در ارتباط با سرور",Toast.LENGTH_SHORT).show();
                _saveButton.setEnabled(true);
                }
            }
        }
}
