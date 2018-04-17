package com.deanoy.user.firebaseauthandconfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends Activity {
    private static int RC_SIGN_IN = 1;
    private static String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private EditText metEmail;
    private EditText metPassword;
    private TextView mtvUserDetails;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Bind UI
        this.metEmail = findViewById(R.id.etEmail);
        this.metPassword = findViewById(R.id.etPassword);
        this.mtvUserDetails = findViewById(R.id.tvUserDetails);
        this.mAuth = FirebaseAuth.getInstance();
        this.mGoogleSignInButton = findViewById(R.id.sign_in_button);
        this.mGoogleSignInButton .setSize(SignInButton.SIZE_STANDARD);
        this.mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onGoogleSignInClick >>");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                Log.e(TAG, "onGoogleSignInClick <<");
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.updateUserDetailsUI(currentUser);
    }

    // On Clicks
    public void onSignOutClick(View v) {
        Log.e(TAG, "onSignoutClick >>");
        mAuth.signOut();
        this.updateUserDetailsUI(mAuth.getCurrentUser());
        Log.e(TAG, "onSignoutClick <<");
    }

    public void onSignUpClick(View v) {
        Log.e(TAG, "onSignUpClick >>");
        mAuth.createUserWithEmailAndPassword(metEmail.getText().toString(), metPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "on mAuth signup complete >>");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUserDetailsUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUserDetailsUI(null);
                        }
                    }
        });
        Log.e(TAG, "onSignUpClick <<");
    }

    public void onSignInClick(View v) {
        Log.e(TAG, "onSignInClick >>");
        mAuth.signInWithEmailAndPassword(metEmail.getText().toString(), metPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUserDetailsUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUserDetailsUI(null);
                        }

                        // ...
                    }
                });
        Log.e(TAG, "onSignInClick <<");
    }

    //Helper functions
    private void updateUserDetailsUI(FirebaseUser currentUser) {
        if(currentUser == null) {
            mtvUserDetails.setText("Signed out");
        } else {
            mtvUserDetails.setText(String.format("Email: %s\n Signed in", currentUser.getDisplayName()));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult >>");
        Log.e(TAG, "Result code: " + resultCode + ". Request code: " + requestCode);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        Log.e(TAG, "onActivityResult <<");
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e(TAG, "signInResult:success");
            updateUserDetailsUI(mAuth.getCurrentUser());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUserDetailsUI(null);
        }
    }
}