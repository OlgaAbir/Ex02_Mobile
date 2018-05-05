package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import Models.Dare;
import Models.DaresAdapter;

public class AllProductsActivity extends Activity {
    private static String TAG = "AllProductsActivity";

    private FirebaseAuth mAuth;
    private RecyclerView mDaresView;
    private DaresAdapter mDaresAdapter;
    private List<Dare> mDaresList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        mAuth = FirebaseAuth.getInstance();
        mDaresView = findViewById(R.id.recyclerView);
        mDaresView.setHasFixedSize(true);
        mDaresView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDaresView.setItemAnimator(new DefaultItemAnimator());

        getAllDares();
    }

    private void getAllDares() {

        mDaresList.clear();
        mDaresAdapter = new DaresAdapter(mDaresList);
        mDaresView.setAdapter(mDaresAdapter);

        //TODO:bind mDaresList to get dares from database
    }

    public void onViewYourProfileClick(View v) {
        Log.e(TAG, "onViewYourProfileClick >>");

        Intent showProfile = new Intent(this, UserProfileActivity.class);

        startActivity(showProfile);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart >>");
        super.onStart();

        if(mAuth.getCurrentUser() == null)
        {
            finish();
        }
    }
}
