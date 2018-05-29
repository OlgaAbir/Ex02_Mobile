package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import Models.AdvancedNotificationData;
import Models.AnalyticsManager;
import Models.Dare;
import Models.Review;
import Models.UserDetails;

public class WriteReviewActivity extends Activity {

    private static final String TAG = "WriteReviewActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mReviewsDatabaseRef;
    private EditText mReviewText;
    private UserDetails mUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate >>" );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        mAuth = FirebaseAuth.getInstance();
        String dareID = getIntent().getStringExtra("dareID");
        mReviewsDatabaseRef = FirebaseDatabase.getInstance().getReference("Reviews").child(dareID);
        mReviewText = findViewById(R.id.etReviewContent);
        getUserDetails();

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

        AnalyticsManager.getInstance().trackAddReview(newReview);

        String completionMsg = "Review was added successfully";

        if(AdvancedNotificationData.getInstance().getReviewBonus() > 0)
        {
            mUserDetails.setBalance(mUserDetails.getBalance() + AdvancedNotificationData.getInstance().getReviewBonus());
            completionMsg += ", Bonus was gained.";
            AdvancedNotificationData.getInstance().setReviewBonus(0);
        }

        displayMessage(completionMsg);
        FirebaseDatabase.getInstance().getReference("UserDetails").child(mAuth.getCurrentUser().getUid()).setValue(mUserDetails);
        finish();

        Log.e(TAG, "onSubmitReview <<" );
    }

    private void getUserDetails() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("UserDetails").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange() >>" );
                mUserDetails = dataSnapshot.getValue(UserDetails.class);
                if(mUserDetails == null) {
                    mUserDetails = new UserDetails();
                }
                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
