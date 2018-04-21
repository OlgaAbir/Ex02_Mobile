package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppHomeScreen extends Activity {

    private static String TAG = "AppHomeScreen";

    private FirebaseAuth mAuth;
    private FirebaseUser mLoggedInUser;
    private TextView mUserName;
    private ImageView mUserProfilePicture;
    private TextView mUserEmail;
    private Button mbtnChangePassword;
    private Button mbtnVerifyEmail;

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
        mUserEmail = findViewById(R.id.tvEmailAddress);
        mbtnChangePassword = findViewById(R.id.btnChangePassword);
        mbtnVerifyEmail = findViewById(R.id.btnVerifyEmail);
        displayLoggedInUserProfile();
    }

    private void displayLoggedInUserProfile()
    {
        if(mLoggedInUser != null && !mLoggedInUser.isAnonymous())
        {
            mbtnVerifyEmail.setVisibility(View.VISIBLE);
            mbtnChangePassword.setVisibility(View.VISIBLE);
            mUserEmail.setVisibility(View.VISIBLE);

            if(mLoggedInUser.getDisplayName() == null || mLoggedInUser.getDisplayName().isEmpty())
            {
                mUserName.setText("Display Name: unconfigured");
            }
            else
            {
                mUserName.setText(mLoggedInUser.getDisplayName());
            }

            if(mLoggedInUser.getPhotoUrl() != null && !mLoggedInUser.getPhotoUrl().toString().isEmpty())
            {
                mUserProfilePicture.setImageURI(mLoggedInUser.getPhotoUrl());
            }

            if(mLoggedInUser.getEmail() != null && !mLoggedInUser.getEmail().isEmpty())
            {
                mUserEmail.setText("Email: " + mLoggedInUser.getEmail());
            }
        }
        else if(mLoggedInUser.isAnonymous()) // Set anonymous user UI
        {
            mUserName.setText("Anonymous");
            mbtnVerifyEmail.setVisibility(View.INVISIBLE);
            mbtnChangePassword.setVisibility(View.INVISIBLE);
            mUserEmail.setVisibility(View.INVISIBLE);
        }
    }

    public void onSignOutClick(View v)
    {
        Log.e(TAG, "onSignoutClick >>");

        mAuth.signOut();
        startDisplayLoginScreen();

        Log.e(TAG, "onSignoutClick <<");
    }

    public void onChangePasswordClick(View v)
    {
        Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);

        startActivity(i);
    }

    public void onVerifyEmailClick(View v)
    {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                displayMessage("A verification message was sent to your mail.");
            }
        });
    }

    private void startDisplayLoginScreen()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(i);
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
