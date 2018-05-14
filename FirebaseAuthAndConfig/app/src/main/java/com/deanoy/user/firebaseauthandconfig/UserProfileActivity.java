package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

import Models.Dare;
import Models.UserDetails;

public class UserProfileActivity extends Activity {

    private static String TAG = "UserProfileActivity";
    private static String FACEBOOK_AUTH = "facebook.com";
    private static String GMAIL_AUTH = "google.com";

    private FirebaseAuth mAuth;
    private FirebaseUser mLoggedInUser;
    private TextView mtvUserName;
    private TextView mtvUserEmail;
    private TextView mtvBalance;
    private ImageView mivUserProfilePicture;
    private Button mbtnChangePassword;
    private Button mbtnVerifyEmail;
    private UserDetails mUserDetails;
    private DatabaseReference mUserDetailsDatabaseRef;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // binding and initialization
        mAuth = FirebaseAuth.getInstance();
        mtvUserName = findViewById(R.id.tvUserNameAppHome);
        mivUserProfilePicture = findViewById(R.id.ivProfilePictureAppHome);
        mLoggedInUser = mAuth.getCurrentUser();
        mtvUserEmail = findViewById(R.id.tvEmailAddressAppHome);
        mbtnChangePassword = findViewById(R.id.btnChangePasswordAppHome);
        mbtnVerifyEmail = findViewById(R.id.btnVerifyEmailAppHome);
        mtvBalance = findViewById(R.id.tvBalance);
        getUserDetails();
        mUserDetailsDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(mLoggedInUser.getUid());
        setUI();
        //tempLoadToDatabase(); //TODO: This is a temp function for creating the dummy dares. Remove before finishing assignment
        //getDaresFromDB();

        Log.e(TAG, "onCreate <<");
    }

   /* @Override
    public void onBackPressed() {
        Log.e(TAG, "onBack >>");
        super.onBackPressed();

        mAuth.signOut();
        LoginManager.getInstance().logOut();
        finish();

        Log.e(TAG, "onBack <<");
    }*/

    //TODO: This is a temp function for creating the dummy dares. Remove before finishing assignment
    /*
    private void tempLoadToDatabase() {
        Dare dare;
        //ArrayList<Dare> daresList = new ArrayList<Dare>();
        DatabaseReference dareRef = FirebaseDatabase.getInstance().getReference("Dares");
        String dbKey;

        for (int i = 0; i < 100; i++) { // Create fake dares
            if (i % 3 == 0) { // Olga
                dare = new Dare("0Aq19XxCdla2b3ymOlDxI1baK283", "olgaabirhaim@gmail.com", "Olga",
                        "Olga's game", "Bomb a national monument", (float) 50, 24, "description.img");
            } else if (i % 3 == 1) { // Dean
                dare = new Dare("YPmMm9PjTTXdxwSWGJCSL8JntXo1", "rustykopo@gmail.com", "Dean",
                        "Dean's trial", "Execute a government official", (float) 2, 48, "description.img");
            } else {
                dare = new Dare("X4SkI6HiHVSUM8cXBcpU9zW3OXt1", "olga_94@live.ru", "Olga2",
                        "No guts no glory", "Steal Idan's Mercedes", (float) 500, 8, "description.img");
            }

            dbKey = dareRef.push().getKey();
            dareRef.child(dbKey).setValue(dare);
        }
    }
    */

    private void setUI() {
        displayLoggedInUserProfile();
        // User signed in using facebook/google.
        if (isUsingAuthMethod(FACEBOOK_AUTH) || isUsingAuthMethod(GMAIL_AUTH) || mLoggedInUser.isAnonymous()) {
            // Set UI accordingly
            mbtnChangePassword.setVisibility(View.INVISIBLE);
            mbtnVerifyEmail.setVisibility(View.INVISIBLE);
            mivUserProfilePicture.setVisibility(View.VISIBLE);

        } else {
            mbtnChangePassword.setVisibility(View.VISIBLE);
            mbtnVerifyEmail.setVisibility(View.VISIBLE);
            mivUserProfilePicture.setVisibility(View.INVISIBLE);
        }
        if (mLoggedInUser.isEmailVerified()) {
            mbtnVerifyEmail.setVisibility(View.INVISIBLE);
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

    private void displayLoggedInUserProfile() {
        if (mLoggedInUser != null && !mLoggedInUser.isAnonymous()) // None anonymous user
        {
            mbtnChangePassword.setVisibility(View.VISIBLE);
            mtvUserEmail.setVisibility(View.VISIBLE);
            Log.e(TAG, "isEmailVerified << " + mLoggedInUser.isEmailVerified());

            if (mLoggedInUser.getDisplayName() == null || mLoggedInUser.getDisplayName().isEmpty()) // no available name
            {
                mtvUserName.setText("Display Name: unconfigured");
            } else {
                Log.e(TAG, "onCreate >> User name: " + mLoggedInUser.getDisplayName());
                mtvUserName.setText(mLoggedInUser.getDisplayName());
            }

            if (mLoggedInUser.getPhotoUrl() != null && !mLoggedInUser.getPhotoUrl().toString().isEmpty()) {
                Log.e(TAG, "onCreate >> User profile pic url: " + mLoggedInUser.getPhotoUrl());

                String imageString = mLoggedInUser.getPhotoUrl().toString();

                if (isUsingAuthMethod(FACEBOOK_AUTH)) {
                    imageString = mLoggedInUser.getPhotoUrl().toString() + "/picture?width=200&height=200";
                }
                if ((isUsingAuthMethod(FACEBOOK_AUTH) || isUsingAuthMethod(GMAIL_AUTH))) {
                    new DownloadImageTask(mivUserProfilePicture)
                            .execute(imageString);
                } else {
                    mivUserProfilePicture.setImageURI(mLoggedInUser.getPhotoUrl());
                }
            }

            if (mLoggedInUser.getEmail() != null && !mLoggedInUser.getEmail().isEmpty()) {
                Log.e(TAG, "onCreate >> User email: " + mLoggedInUser.getEmail());
                mtvUserEmail.setText("Email: " + mLoggedInUser.getEmail());
            }
        } else if (mLoggedInUser.isAnonymous()) // Set anonymous user UI
        {
            mtvUserName.setText("Anonymous");
            mbtnVerifyEmail.setVisibility(View.INVISIBLE);
            mbtnChangePassword.setVisibility(View.INVISIBLE);
            mtvUserEmail.setVisibility(View.INVISIBLE);
            mtvBalance.setVisibility(View.INVISIBLE);
        }
    }

    public void onSignOutClick(View v) {
        Log.e(TAG, "onSignoutClick >>");

        signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
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

    public void onChangePasswordClick(View v) {
        Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);

        startActivity(i);
    }

    public void onVerifyEmailClick(View v) {
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

    private void getUserDetails() {
        mLoggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mLoggedInUser.getUid();
        FirebaseDatabase.getInstance().getReference("UserDetails").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange() >>" );
                mUserDetails = dataSnapshot.getValue(UserDetails.class);
                if(mUserDetails == null) {
                    mUserDetails = new UserDetails();
                }

                if(!mLoggedInUser.isAnonymous())
                    mtvBalance.setText("Balance: " + mUserDetails.getBalance());


                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
