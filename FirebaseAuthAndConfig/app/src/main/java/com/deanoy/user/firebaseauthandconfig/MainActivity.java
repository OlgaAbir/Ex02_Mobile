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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    private static int RC_SIGN_IN = 1;
    private static String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private EditText metEmail;
    private EditText metPassword;
    private TextView mtvUserDetails;
    private GoogleSignInOptions mGoogleSignInOptions;
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
        this.mGoogleSignInButton = findViewById(R.id.btnGoogleSignIn);
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
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.updateUserDetailsUI(currentUser);
    }

    // On Clicks
    public void onSignOutClick(View v)
    {
        Log.e(TAG, "onSignoutClick >>");

        mAuth.signOut();
        this.updateUserDetailsUI(mAuth.getCurrentUser());

        Log.e(TAG, "onSignoutClick <<");
    }

    public void onSignUpClick(View v)
    {
        Log.e(TAG, "onSignUpClick >>");

        if(isValidInputCredentials())
        {
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
        }

        Log.e(TAG, "onSignUpClick <<");
    }

    public void onSignInClick(View v)
    {
        Log.e(TAG, "onSignInClick >>");

        if(isValidInputCredentials())
        {
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
        }
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

    private void displayMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean isValidInputCredentials()
    {
        boolean result = true;

        if(metEmail.getText().toString().isEmpty())
        {
            displayMessage("You should enter your email in the email field");
            result = false;
        }

        if(result && !isValidEmailAddress())
        {
            displayMessage("The Email you entered is not in correct email format");
            result = false;
        }

        if(result && metPassword.getText().toString().isEmpty())
        {
            displayMessage("You should enter your password in the password field");
            result = false;
        }

        if(result && !isValidPassword())
        {
            displayMessage("You should enter a password that contains at least 6 characters");
            result = false;
        }
        return result;
    }

    private boolean isValidEmailAddress()
    {
        String email = metEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Boolean result = false;

        if(email != null)
        {
            result = isMatchingPattern(email, emailPattern);
        }

        return result;
    }

    private boolean isValidPassword()
    {
        String password = metPassword.getText().toString();

        return password != null && password.length() >= 6;
    }

    private boolean isMatchingPattern(String value, String patternStr)
    {
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}