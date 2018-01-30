package com.example.navid.androidproject.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.GridView;
import android.widget.TextView;

import com.example.navid.androidproject.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import com.example.navid.androidproject.Activity.NoConnectionActivity;
import com.example.navid.androidproject.Activity.SubCategoryActivity;
import com.example.navid.androidproject.Adapter.CategoryImageAdapter;
import com.example.navid.androidproject.Adapter.SubCategoryListAdapter;
import com.example.navid.androidproject.Other.Category;
import com.example.navid.androidproject.Other.InternetConnection;
import com.example.navid.androidproject.Other.StaticInfo;

public class CategoryFragment extends Fragment {

    public ArrayList<Category> categories;

    private RecyclerView recyclerView;
    private CategoryImageAdapter categoryImageAdapter;

    private InternetConnection internetConnection;

    private String title;
    private int page;

    private TextView catTitle;

//    public static CategoryFragment newInstance(int page, String title) {
//        CategoryFragment categoryFragment = new CategoryFragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        categoryFragment.setArguments(args);
//        return categoryFragment;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        page = getArguments().getInt("someInt", 0);
//        title = getArguments().getString("someTitle");

        internetConnection = new InternetConnection(getContext());
        categories = new ArrayList<>();
        categories.add(new Category("کالای دیجیتال" , R.drawable.digital));
        categories.add(new Category("مد و لباس" , R.drawable.fashion));
        categories.add(new Category("لوازم خانه" , R.drawable.home));
        categories.add(new Category("زیبایی و سلامت" , R.drawable.beauti));
        categories.add(new Category("سرگرمی و ورزش" , R.drawable.sport));
        categories.add(new Category("فرهنگ و هنر" , R.drawable.calture));
        categories.add(new Category("ابزار و خودرو" , R.drawable.car));
        categories.add(new Category("مواد غذایی و خوراکی" , R.drawable.food));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_category , container , false);

        recyclerView = (RecyclerView) myView.findViewById(R.id.categoryRecyclerView);
        categoryImageAdapter = new CategoryImageAdapter(myView.getContext() , categories , this);
        recyclerView.setLayoutManager(new GridLayoutManager(myView.getContext(),2));
        recyclerView.setAdapter(categoryImageAdapter);

        return myView;
    }

    public void openNewActivity(){
        if(internetConnection.haveNetworkConnection()) {
            Intent intent = new Intent(getContext(), SubCategoryActivity.class);
            //intent.putExtra("categoryName", selectedCategoryName);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        else{
            Intent intent = new Intent(getContext() , NoConnectionActivity.class);
            intent.putExtra("activity" , 6);
            startActivity(intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

//    public void animateIntent(View view){
//        Intent intent = new Intent(getContext() , SubCategoryActivity.class);
//        String transitionName = getString(R.string.transition_string);
//        View viewStart = getActivity().findViewById(R.id.catCardView);
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity() , viewStart , transitionName);
//        ActivityCompat.startActivity(getContext(), intent , options.toBundle());
//    }
}
