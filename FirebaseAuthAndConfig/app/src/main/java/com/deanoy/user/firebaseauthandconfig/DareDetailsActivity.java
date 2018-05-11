package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import Models.Dare;
import Models.Review;
import Models.ReviewsAdapter;
import Models.UserDetails;

public class DareDetailsActivity extends Activity {

    private static final String TAG = "DareDetailsActivity";
    private static int GALLERY = 1;

    private Dare mSelectedDare;
    private RecyclerView mReviewsView;
    private DatabaseReference mReviewsDatabaseRef;
    private List<Review> mReviewsList =  new ArrayList<>();
    private FirebaseUser mLoggedInUser;
    private DatabaseReference mUserDatabaseRef;
    private StorageReference mStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate >>" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_details);
        mSelectedDare = getIntent().getParcelableExtra("dare");
        Log.e(TAG, "onCreate >> Dare =" + mSelectedDare.toString());
        mReviewsDatabaseRef = FirebaseDatabase.getInstance().getReference("Reviews").child(mSelectedDare.getDareId());
        mLoggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mLoggedInUser.getUid();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(userID);

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

        if(UserDetails.DaresIDs.contains(mSelectedDare.getDareId())){
            ((Button) findViewById(R.id.btnBuy)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnBought)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnAddReview)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.btnUploadPhoto)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.btnSavePhoto)).setVisibility(View.VISIBLE);
        }
        else{
            ((Button) findViewById(R.id.btnAddReview)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnSavePhoto)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnUploadPhoto)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnBought)).setVisibility(View.INVISIBLE);
        }
        Log.e(TAG, "DareID : " + mSelectedDare.getDareId());
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

        if(mLoggedInUser.isAnonymous()){
            Toast.makeText(this, "Please sign up or sign in to proceed this action", Toast.LENGTH_SHORT).show();
        }
        else{
            if(UserDetails.mBalance >= mSelectedDare.getBuyInCost()){
                mUserDatabaseRef.child(mLoggedInUser.getUid()).child(mSelectedDare.getDareId()).setValue(mSelectedDare.getDareId());
                mUserDatabaseRef.child(mLoggedInUser.getUid()).child("balance").setValue(UserDetails.mBalance);
                Toast.makeText(this, "You have successfully purchased this dare", Toast.LENGTH_SHORT).show();
                ((Button) findViewById(R.id.btnBuy)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.btnAddReview)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.btnSavePhoto)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.btnUploadPhoto)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.btnBought)).setVisibility(View.INVISIBLE);

                UserDetails.mBalance -= mSelectedDare.getBuyInCost();
            }
            else{
                Toast.makeText(this, "You dont have enough cash to buy this dare", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "onBuyClick() <<");
        }
    }

    public void onBoughtClick(View view){
        Toast.makeText(this, "You have already purchased this dare", Toast.LENGTH_SHORT).show();
    }

    public void onSavePhotoClick(View view){
        ((Button) findViewById(R.id.btnBuy)).setVisibility(View.INVISIBLE);
        ((Button) findViewById(R.id.btnAddReview)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.btnSavePhoto)).setVisibility(View.INVISIBLE);
        ((Button) findViewById(R.id.btnUploadPhoto)).setVisibility(View.INVISIBLE);
        ((Button) findViewById(R.id.btnBought)).setVisibility(View.VISIBLE);

        Toast.makeText(this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
    }

    public void onAddImageClick(View view){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        if (requestCode == GALLERY) {
//            if (imageReturnedIntent != null) {
//                Uri contentURI = imageReturnedIntent.getData();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    //uploadImage(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(DareDetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
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
            Log.e(TAG, "Successfully uploaded image to storage.");
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
}
