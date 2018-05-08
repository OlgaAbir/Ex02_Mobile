package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import Models.Dare;
import Models.Review;

public class WriteReviewActivity extends Activity {

    private static final String TAG = "WriteReviewActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mReviewsDatabaseRef;
    private EditText mReviewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate >>" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        mAuth = FirebaseAuth.getInstance();
        String dareID = getIntent().getStringExtra("dareID");
        mReviewsDatabaseRef = FirebaseDatabase.getInstance().getReference("Reviews").child(dareID);
        mReviewText = findViewById(R.id.etReviewContent);

        Log.e(TAG, "onCreate() <<" );
    }

    public void onSubmitReviewClick(View view)
    {
        Log.e(TAG, "onSubmitReview >>" );

        Review newReview = new Review();
        newReview.setCreationDate(new Date());
        newReview.setText(mReviewText.getText().toString());
        newReview.setWriterID(mAuth.getCurrentUser().getUid());
        newReview.setWriterName(mAuth.getCurrentUser().getDisplayName());

        String newReviewKey = mReviewsDatabaseRef.push().getKey();
        mReviewsDatabaseRef.child(newReviewKey).setValue(newReview);

        Log.e(TAG, "onSubmitReview <<" );
    }
}
