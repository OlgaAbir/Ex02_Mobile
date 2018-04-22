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

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class AppHomeScreen extends Activity {

    private static String TAG = "AppHomeScreen";
    private static String FACEBOOK_AUTH = "facebook.com";
    private static String GMAIL_AUTH = "google.com";


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
        Log.e(TAG, "onCreate >>");

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
        setUI();

        Log.e(TAG, "onCreate <<");
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy >>");

        super.onDestroy();
        signOut();

        Log.e(TAG, "onDestroy <<");
    }

    private void setUI() {
        displayLoggedInUserProfile();
        // User signed in using facebook/google.
        if(isUsingAuthMethod(FACEBOOK_AUTH) || isUsingAuthMethod(GMAIL_AUTH)) {
            // Set UI accordingly
            mbtnChangePassword.setVisibility(View.INVISIBLE);
        } else {
            mbtnChangePassword.setVisibility(View.VISIBLE);
        }
    }

    // Check if the user is logged in using the auth type in the parameter
    private boolean isUsingAuthMethod(String authType) {
        boolean isUsingAuthMethod = false;

        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals(authType)) {
                Log.e(TAG, "User is signed in with " + authType);
                isUsingAuthMethod = true;
            }
        }

        return isUsingAuthMethod;
    }

    private void displayLoggedInUserProfile()
    {
        if(mLoggedInUser != null && !mLoggedInUser.isAnonymous()) // None anonymous user
        {
            mbtnVerifyEmail.setVisibility(View.VISIBLE);
            mbtnChangePassword.setVisibility(View.VISIBLE);
            mUserEmail.setVisibility(View.VISIBLE);

            if(mLoggedInUser.getDisplayName() == null || mLoggedInUser.getDisplayName().isEmpty()) // no available name
            {
                mUserName.setText("Display Name: unconfigured");
            }
            else
            {
                Log.e(TAG, "onCreate >> User name: " + mLoggedInUser.getDisplayName());
                mUserName.setText(mLoggedInUser.getDisplayName());
            }

            if(mLoggedInUser.getPhotoUrl() != null && !mLoggedInUser.getPhotoUrl().toString().isEmpty())
            {
                Log.e(TAG, "onCreate >> User profile pic url: " + mLoggedInUser.getPhotoUrl());
                mUserProfilePicture.setImageURI(mLoggedInUser.getPhotoUrl());
            }

            if(mLoggedInUser.getEmail() != null && !mLoggedInUser.getEmail().isEmpty())
            {
                Log.e(TAG, "onCreate >> User email: " + mLoggedInUser.getEmail());
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

        signOut();
        finish();

        Log.e(TAG, "onSignoutClick <<");
    }

    private void signOut() {
        Log.e(TAG, "signOut >>");

        mAuth.signOut();
        // Log out from facebook
        LoginManager.getInstance().logOut();

        Log.e(TAG, "signOut <<");
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

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
