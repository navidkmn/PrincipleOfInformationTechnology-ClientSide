package com.example.navid.androidproject.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.navid.androidproject.R;

import com.example.navid.androidproject.Activity.CardActivity;
import com.example.navid.androidproject.Activity.InterestsActivity;
import com.example.navid.androidproject.Activity.LoginActivity;
import com.example.navid.androidproject.Activity.MainActivity;
import com.example.navid.androidproject.Activity.NoConnectionActivity;
import com.example.navid.androidproject.Activity.StoreActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyDialogFragment extends DialogFragment {

    public static MyDialogFragment newInctance(String activity, String title , String buttonName , int image){
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("activity" , activity);
        bundle.putString("title", title);
        bundle.putString("buttonName", buttonName);
        bundle.putInt("image" , image);
        myDialogFragment.setArguments(bundle);
        return myDialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final String activity = getArguments().getString("activity");
        String title = getArguments().getString("title");
        String buttonName = getArguments().getString("buttonName");
        int image = getArguments().getInt("image");

        AlertDialog dialog = null;
        switch (activity){
            case "StoreActivity":
                dialog = new AlertDialog.Builder(getActivity() , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setIcon(image)
                        .setTitle(title)
                        .setPositiveButton(buttonName,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        ((StoreActivity)
                                                getActivity()).doPositiveClick();
                                    }
                                }).create();
                break;
            case "CartActivity":
                dialog = new AlertDialog.Builder(getActivity() , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setIcon(image)
                        .setTitle(title)
                        .setPositiveButton(buttonName,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        ((CardActivity)
                                                getActivity()).doPositiveClick();
                                    }
                                }).create();
                break;
            case "MainActivity":
                dialog = new AlertDialog.Builder(getActivity() , AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setIcon(image)
                        .setTitle(title)
                        .setPositiveButton(buttonName,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dialog.dismiss();
                                    }
                                }).create();
                break;
            default:
                break;
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(activity == "InterestsActivity") {
                        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
                return false;
            }
        });
        return dialog;
    }
}