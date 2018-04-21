package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends Activity {

    private FirebaseAuth mAuth;
    private EditText mCurrentPassword;
    private EditText mNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        mCurrentPassword = findViewById(R.id.etCurrentPassword);
        mNewPassword = findViewById(R.id.etNewPassword);
    }

    public void onSaveButtonClick(View v)
    {
        boolean isValidInput = true;

        if(mCurrentPassword.getText().toString().isEmpty()) {
            displayMessage("You must enter current password to continue");
            isValidInput = false;
        }

        if(isValidInput && mNewPassword.getText().toString().isEmpty())
        {
            displayMessage("You must enter new password to continue");
            isValidInput = false;
        }

        if(!isValidPassword(mNewPassword.getText().toString()))
        {
            displayMessage("The new Password should have at least 6 characters");
            isValidInput = false;
        }

        if (isValidInput)
        {
            mAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(
                    mAuth.getCurrentUser().getEmail(),mCurrentPassword.getText().toString()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mAuth.getCurrentUser().updatePassword(mNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        displayMessage("Password updated");
                                    }
                                });
                            }
                            else
                            {
                                displayMessage("Could not change password because the current password you have entered is not correct");
                            }
                        }
                    });
        }
    }

    private boolean isValidPassword(String password) {

        return !password.isEmpty() && password.length() >= 6;
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
