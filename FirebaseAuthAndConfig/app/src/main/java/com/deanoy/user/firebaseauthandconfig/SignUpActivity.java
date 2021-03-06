package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity {
    private static String TAG = "SignUpActivity";
    private static final String EMAIL_DATA = "email_data";
    private static final String PASSWORD_DATA = "password_data";

    private FirebaseAuth mAuth;
    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private CheckBox mNotRobotCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        // Bind UI
        mAuth = FirebaseAuth.getInstance();
        mDisplayName = findViewById(R.id.etDisplayName);
        mEmail = findViewById(R.id.etEmailMainActivity);
        mPassword = findViewById(R.id.etPasswordMainActivity);
        mNotRobotCheckBox = findViewById(R.id.cbNotRobot);

        // Init UI
        Intent intent = getIntent();
        String email = intent.getStringExtra(EMAIL_DATA);
        mEmail.setText(email);
        String password = intent.getStringExtra(PASSWORD_DATA);
        mPassword.setText(password);
    }

    public void onSignUpClick(View v) {
        Log.e(TAG, "onSignUpClick >>");

        if(isValidSignUpFields())
        {
            mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            Log.e(TAG, "on mAuth signup complete >>");
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                showSignUpResult(user);
                            }
                            else
                            {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "createUserWithEmail:failure", task.getException());
                                showSignUpResult(null);
                            }
                        }
                    });
        }

        Log.e(TAG, "onSignUpClick <<");
    }


    private void showSignUpResult(FirebaseUser user)
    {
        Log.e(TAG, "showSignUpResult >>");

        if(user == null)
        {
            displayMessage("Authentication failed, try again");
        }
        else
        {
            updateFirebaseUserInfo(user);
            displayMessage("Signed Up successfully");

        }

        Log.e(TAG, "showSignUpResult <<");
    }

    private void updateFirebaseUserInfo(FirebaseUser user)
    {
        Log.e(TAG, "updateFirebaseUserInfo >>");

        UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder();

        if(mDisplayName.getText() != null) {
            profileBuilder.setDisplayName(mDisplayName.getText().toString());
        }

        user.updateProfile(profileBuilder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                startActivity(i);
            }
        });

        Log.e(TAG, "updateFirebaseUserInfo <<");
    }

    private boolean isValidSignUpFields()
    {
        Log.e(TAG, "isValidSignUpFields >>");
        boolean result = true;

        if(mDisplayName.getText().toString().isEmpty())
        {
            displayMessage("You should enter your display name in the display name field");
            result = false;
        }

        if(result && mEmail.getText().toString().isEmpty())
        {
            displayMessage("You should enter your email in the email field");
            result = false;
        }

        if(result && !isValidEmailAddress())
        {
            displayMessage("The Email you entered is not in correct email format");
            result = false;
        }

        if(result && mPassword.getText().toString().isEmpty())
        {
            displayMessage("You should enter your password in the password field");
            result = false;
        }

        if(result && !isValidPassword())
        {
            displayMessage("You should enter a password that contains at least 6 characters");
            result = false;
        }

        if(result && !mNotRobotCheckBox.isChecked())
        {
            displayMessage("You must Check the not robot checkbox before continue");
            result = false;
        }

        Log.e(TAG, "isValidSignUpFields <<");

        return result;
    }

    private boolean isValidEmailAddress() {
        String email = mEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Boolean result = false;

        if(!email.isEmpty())
        {
            result = isMatchingPattern(email, emailPattern);
        }

        return result;
    }

    private boolean isValidPassword() {
        String password = mPassword.getText().toString();

        return !password.isEmpty() && password.length() >= 6;
    }

    private boolean isMatchingPattern(String value, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
