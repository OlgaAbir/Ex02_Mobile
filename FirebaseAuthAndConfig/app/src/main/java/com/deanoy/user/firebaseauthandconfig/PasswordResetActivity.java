package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends Activity {
    private static final String TAG = "PasswordResetActivity";
    private static final String EMAIL_DATA = "email_data";
    private EditText metEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        bindUI();
        Intent intent = getIntent();
        String email = intent.getStringExtra(EMAIL_DATA);
        metEmail.setText(email);

        Log.e(TAG, "onCreate <<");
    }

    public void onResetPasswordClick(View v) {
        Log.e(TAG, "OnResetPasswordClick >>");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String emailAddress = metEmail.getText().toString();
        Log.e(TAG, "OnResetPasswordClick >> user email: " + emailAddress);
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Email sent.");
                            Toast.makeText(getApplicationContext(), "Email sent to: " + emailAddress,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send an email to: " + emailAddress,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Log.e(TAG, "OnResetPasswordClick <<");
    }

    private void bindUI(){
        metEmail = findViewById(R.id.etUserEmail);
    }
}
