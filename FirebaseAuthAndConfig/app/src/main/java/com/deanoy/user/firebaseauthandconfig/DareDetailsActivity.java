package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Models.Dare;
import Models.Review;
import Models.ReviewsAdapter;

public class DareDetailsActivity extends Activity {

    private static final String TAG = "DareDetailsActivity";

    private Dare mSelectedDare;
    private RecyclerView mReviewsView;
    private DatabaseReference mReviewsDatabaseRef;
    private List<Review> mReviewsList =  new ArrayList<>();
    private boolean mIsPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate >>" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_details);
        mSelectedDare = getIntent().getParcelableExtra("dare");
        Log.e(TAG, "onCreate >> Dare =" + mSelectedDare.toString());
        mReviewsDatabaseRef = FirebaseDatabase.getInstance().getReference("Reviews").child(mSelectedDare.getDareId());

        ((TextView) findViewById(R.id.tvDetailsDareName)).setText("Name: " + mSelectedDare.getDareName());
        ((TextView) findViewById(R.id.tvDetailsPublisher)).setText("Publisher: " + mSelectedDare.getCreaterName());
        ((TextView) findViewById(R.id.tvDetailsPrice)).setText("Price: " + mSelectedDare.getBuyInCost());
        ((TextView) findViewById(R.id.tvDetailsProfit)).setText("Profit: " + mSelectedDare.getProfit());
        ((TextView) findViewById(R.id.tvDetailsDescription)).setText("I dare you to: " + mSelectedDare.getDescription());

        mReviewsView = findViewById(R.id.dare_reviews);
        mReviewsView .setHasFixedSize(true);
        mReviewsView .setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mReviewsView .setItemAnimator(new DefaultItemAnimator());

        getReviews();
        Log.e(TAG, "onCreate <<" );
    }

    private void getReviews()
    {
        mReviewsList.clear();
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(mReviewsList);
        mReviewsView.setAdapter(reviewsAdapter);

        getReviewsFromDB();
    }

    private void getReviewsFromDB()
    {
        mReviewsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange() >>" );
                Review review;
                mReviewsList.clear();
                for (DataSnapshot reviewSnapshot: dataSnapshot.getChildren()) {
                    review = reviewSnapshot.getValue(Review.class);
                    Log.e(TAG, "#$#Review: " + review.toString()); // Print for debugging TODO: remove before assigning
                    mReviewsList.add(review);
                    // newer review will appear first
                    mReviewsList.sort( new Comparator<Review>() {
                                           public int compare(Review r1, Review r2) {
                                               if (r1.getCreationDate().after(r2.getCreationDate())) {

                                                   return  -1;
                                               }

                                               return 1;
                                           }
                                       });
                }

                mReviewsView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void onBuyClick(View v)
    {
        // TODO: Idan implement please all that relevant to buy process :
        // - the things that mentioned in the word file I sent in the group
        // - if the user is anonymous it's time to force him to sign up/sign in to proceed with buying
        // - the write review button should be invisible until the user buy's the dare. after he buy's make it visible/available
    }

    public void onAddReviewClick(View v)
    {
        Log.e(TAG, "onAddReviewClick() >>" );

        Intent writeReviewIntent = new Intent(getApplicationContext(), WriteReviewActivity.class);
        writeReviewIntent.putExtra("dareID", mSelectedDare.getDareId());
        startActivity(writeReviewIntent);

        Log.e(TAG, "onAddReviewClick() << ");
    }
}
