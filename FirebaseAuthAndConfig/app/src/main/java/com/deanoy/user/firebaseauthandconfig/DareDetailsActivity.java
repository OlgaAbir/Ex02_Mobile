package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import Models.Dare;
import Models.Review;
import Models.ReviewsAdapter;

public class DareDetailsActivity extends Activity {

    private Dare mSelectedDare;
    private RecyclerView mReviewsView;
    private DatabaseReference mReviewsDatabaseRef;
    private List<Review> mReviewsList =  new ArrayList<>();
    private boolean mIsPurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_details);

        mSelectedDare = getIntent().getParcelableExtra("dare");

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
        // TODO: Dean implement this please
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
        // TODO: I (Olga) will implement this :
        // - create write review activity for this
    }
}
