package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppHomeScreen extends Activity {

    private static String TAG = "AppHomeScreen";

    private FirebaseAuth mAuth;
    private FirebaseUser mLoggedInUser;
    private TextView mUserName;
    private ImageView mUserProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home_screen);

        // binding and initialization
        mAuth = FirebaseAuth.getInstance();
        mUserName =  findViewById(R.id.tvUserName);
        mUserProfilePicture = findViewById(R.id.ivProfilePicture);
        mLoggedInUser = mAuth.getCurrentUser();

        displayLoggedInUserProfile();
    }

    private void displayLoggedInUserProfile()
    {
        if(mLoggedInUser != null)
        {
            if(mLoggedInUser.getDisplayName() == null || mLoggedInUser.getDisplayName().isEmpty())
            {
                mUserName.setText("Display Name: unconfigured");
            }
            else
            {
                mUserName.setText(mLoggedInUser.getDisplayName());
            }

            if(mLoggedInUser.getPhotoUrl() != null)
            {
                mUserProfilePicture.setImageURI(mLoggedInUser.getPhotoUrl());
            }
        }
    }

    public void onSignOutClick(View v)
    {
        Log.e(TAG, "onSignoutClick >>");

        mAuth.signOut();
        startDisplayLoginScreen();

        Log.e(TAG, "onSignoutClick <<");
    }

    private void startDisplayLoginScreen()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
    }
}
