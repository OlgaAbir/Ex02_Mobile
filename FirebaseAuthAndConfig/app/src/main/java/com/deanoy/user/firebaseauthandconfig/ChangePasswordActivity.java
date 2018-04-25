package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends Activity {

    private FirebaseAuth mAuth;
    private EditText metCurrentPassword;
    private EditText metNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        metCurrentPassword = findViewById(R.id.etCurrentPasswordChangePassword);
        metNewPassword = findViewById(R.id.etNewPasswordChangePassword);
    }

    public void onSaveButtonClick(View v)
    {
        boolean isValidInput = true;

        if(metCurrentPassword.getText().toString().isEmpty()) {
            displayMessage("You must enter current password to continue");
            isValidInput = false;
        }

        if(isValidInput && metNewPassword.getText().toString().isEmpty())
        {
            displayMessage("You must enter new password to continue");
            isValidInput = false;
        }

        if(!isValidPassword(metNewPassword.getText().toString()))
        {
            displayMessage("The new Password should have at least 6 characters");
            isValidInput = false;
        }

        if (isValidInput)
        {
            mAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(
                    mAuth.getCurrentUser().getEmail(), metCurrentPassword.getText().toString()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mAuth.getCurrentUser().updatePassword(metNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
