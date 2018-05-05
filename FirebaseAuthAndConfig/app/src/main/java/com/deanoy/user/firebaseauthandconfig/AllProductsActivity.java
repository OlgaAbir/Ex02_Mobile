package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class AllProductsActivity extends Activity {
    private static String TAG = "AllProductsActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        mAuth = FirebaseAuth.getInstance();
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
