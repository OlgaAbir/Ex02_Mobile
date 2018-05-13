package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Delayed;

import Models.Dare;
import Models.Review;
import Models.ReviewsAdapter;
import Models.UserDetails;

public class DareDetailsActivity extends Activity {

    private static final String TAG = "DareDetailsActivity";
    private static final String DARE_DATA = "dare";
    private static int GALLERY = 1;

    private Dare mSelectedDare;
    private UserDetails mUserDetails;
    private RecyclerView mReviewsView;
    private DatabaseReference mReviewsDatabaseRef;
    private List<Review> mReviewsList =  new ArrayList<>();
    private FirebaseUser mLoggedInUser;
    private DatabaseReference mUserDetailsDatabaseRef;
    private DatabaseReference mDareDatabaseRef;
    private StorageReference mStorageReference;
    private FirebaseAuth mAuth;



    //TODO: Idan do like this
    // UI
    private TextView mtvDetailsDareName;
    private TextView mtvDetailsPublisher;
    private TextView mtvDetailsPrice;
    private TextView mtvDetailsProfit;
    private TextView mtvDetailsDescription;

    private Button mbtnActionButton;
    private Button mbtnAddReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_details);
        mSelectedDare = getIntent().getParcelableExtra(DARE_DATA);
        getUserDetails();
        Log.e(TAG, "onCreate >> Dare =" + mSelectedDare.toString());
        mReviewsDatabaseRef = FirebaseDatabase.getInstance().getReference("Reviews").child(mSelectedDare.getDareId());
        mUserDetailsDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(mLoggedInUser.getUid());
        mDareDatabaseRef = FirebaseDatabase.getInstance().getReference("Dares").child(mSelectedDare.getDareId());
        mAuth = FirebaseAuth.getInstance();

        // Set UI
        setUI();

        getReviews();

        Log.e(TAG, "onCreate <<" );
    }

    private void setUI() {

        mbtnActionButton = findViewById(R.id.btnAction);
        mbtnAddReview = findViewById(R.id.btnAddReview);
        mReviewsView = findViewById(R.id.dare_reviews);
        mtvDetailsDareName = (TextView) findViewById(R.id.tvDetailsDareName);
        mtvDetailsPublisher = (TextView) findViewById(R.id.tvDetailsPublisher);
        mtvDetailsPrice = (TextView) findViewById(R.id.tvDetailsPrice);
        mtvDetailsProfit = (TextView) findViewById(R.id.tvDetailsProfit);
        mtvDetailsDescription = (TextView) findViewById(R.id.tvDetailsDescription);

        if(mLoggedInUser.getUid().contentEquals(mSelectedDare.getCreaterID()) || mSelectedDare.getCompletedUserIds().contains(mLoggedInUser.getUid())) {
            Log.e(TAG, "setUI << Don't show action button" );
            mbtnActionButton.setVisibility(View.INVISIBLE); // User is the creater or user already completed this dare. No action button needed
        } else if (!mLoggedInUser.isAnonymous()){ // Not anonymous
            String btnText;
            if (!mSelectedDare.getAttemptingUserID().contains(mLoggedInUser.getUid())){ // User is buying dare
                Log.e(TAG, "setUI << User still did not purchase dare" );
                btnText = "Buy";
                mbtnAddReview.setVisibility(View.INVISIBLE);

            } else { // User is uploading completion image
                Log.e(TAG, "setUI << User already purchased dare" );
                btnText = "Upload Picture";
                mbtnAddReview.setVisibility(View.INVISIBLE);
            }

            mbtnActionButton.setText(btnText);
        } else { // User is anonymous
            Log.e(TAG, "setUI << User is anonymous, hide add review button");
            mbtnAddReview.setVisibility(View.INVISIBLE);
            mbtnActionButton.setText("Buy");
        }

        mtvDetailsDareName.setText("Name: " + mSelectedDare.getDareName());
        mtvDetailsPublisher.setText("Publisher: " + mSelectedDare.getCreaterName());
        mtvDetailsPrice.setText("Price: " + mSelectedDare.getBuyInCost());
        mtvDetailsProfit.setText("Profit: " + mSelectedDare.getProfit());
        mtvDetailsDescription.setText("I dare you to: " + mSelectedDare.getDescription());

        mReviewsView .setHasFixedSize(true);
        mReviewsView .setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mReviewsView .setItemAnimator(new DefaultItemAnimator());
    }

    private void getUserDetails() {
        mLoggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mLoggedInUser.getUid();
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
                    Log.e(TAG, "Review: " + review.toString());
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

    public void onActionClick(View v)
    {
        // TODO: IDAN implement please all that relevant to buy process :
        // Done - the things that mentioned in the word file I sent in the group
        // Done - if the user is anonymous it's time to force him to sign up/sign in to proceed with buying
        // Done - the write review button should be invisible until the user buy's the dare. after he buy's make it visible/available
        if(mLoggedInUser.isAnonymous()){
            Toast.makeText(this, "You are not allowed to buy this dare ,please sign in/sign up.", Toast.LENGTH_LONG).show();
            signOut();
            Intent i = new Intent(this ,MainActivity.class);
            startActivity(i);
        }

        if(mUserDetails == null) {
            Toast.makeText(this, "Service not available yet. Please try again soon", Toast.LENGTH_SHORT).show();
            return; // Not ready yet
        } else if (!mSelectedDare.getAttemptingUserID().contains(mLoggedInUser.getUid())){ // User is buying dare
            handlePurchase();
        } else { // User is uploading completion image
            handleUploadCompletionImage();
        }
    }

    private void handleUploadCompletionImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void handlePurchase() {
        int balance = mUserDetails.getBalance();

        Log.e(TAG,mUserDetails.toString());
        if(balance >= mSelectedDare.getBuyInCost()) {
            mUserDetails.setBalance(balance - mSelectedDare.getBuyInCost());
            mUserDetails.getPurchasedDareIds().add(mSelectedDare.getDareId()); // Add dare to purchased
            mSelectedDare.getAttemptingUserID().add(mLoggedInUser.getUid());
            setUI();
            updateDB();
        } else {
            Toast.makeText(this, "You cannot afford this dare", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateDB() {
        mDareDatabaseRef.setValue(mSelectedDare);
        mUserDetailsDatabaseRef.setValue(mUserDetails);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult <<");
        if (resultCode == RESULT_OK && requestCode == GALLERY && data != null) {
            try {
                Uri contentURI = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                uploadImage(bitmap);
                Log.e(TAG, "onActivityResult << Selecting photo");
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult << Error! Unable to retrieve photo");
                e.printStackTrace();
            }
        }

        Log.e(TAG, "onActivityResult >>");
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload image
        Log.e(TAG, "Uploading image...");
        //StorageReference imageRef = mStorageRef.child(userId).child(Integer.toString(imageNumber));
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference("DareDetails").child(mSelectedDare.getDareId()).child(userId).child("CompletionImage");
        UploadTask uploadTask = mStorageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(DareDetailsActivity.this, "Failed uploading image.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed uploading to storage.");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dareCompleted();
                Log.e(TAG, "Successfully uploaded image to storage.");
                mbtnAddReview.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onAddReviewClick(View v)
    {
        Log.e(TAG, "onAddReviewClick() >>" );

        Intent writeReviewIntent = new Intent(getApplicationContext(), WriteReviewActivity.class);
        writeReviewIntent.putExtra("dareID", mSelectedDare.getDareId());
        startActivity(writeReviewIntent);

        Log.e(TAG, "onAddReviewClick() << ");
    }

    private void dareCompleted() {
        mUserDetails.setBalance(mUserDetails.getBalance() + mSelectedDare.getProfit()); // Set new balance
        mUserDetails.getPurchasedDareIds().remove(mSelectedDare.getDareId()); // Remove dare from users purchased list
        mUserDetails.getCompletedDareIds().add(mSelectedDare.getDareId()); // Add dare to users completed dares list
        mSelectedDare.getAttemptingUserID().remove(mLoggedInUser.getUid()); // Remove user from attempting user ids list
        mSelectedDare.getCompletedUserIds().add(mLoggedInUser.getUid()); // Add user to completed user ids list
        setUI();
        updateDB();
    }

    private void signOut() {
        Log.e(TAG, "signOut >>");

        mAuth.signOut();
        // Log out from facebook
        LoginManager.getInstance().logOut();

        Log.e(TAG, "signOut <<");
    }
}
