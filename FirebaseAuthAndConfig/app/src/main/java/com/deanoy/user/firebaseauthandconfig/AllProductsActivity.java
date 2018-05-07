package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.Dare;
import Models.DaresAdapter;

import static java.lang.Thread.sleep;

public class AllProductsActivity extends Activity {
    private static String TAG = "AllProductsActivity";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase ;
    private DatabaseReference mDatabaseDaresRef ;
    private RecyclerView mDaresView;
    private DaresAdapter mDaresAdapter;
    private List<Dare> mDaresList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseDaresRef  = mDatabase.getReference("Dares");

        mDaresView = findViewById(R.id.recyclerView);
        mDaresView.setHasFixedSize(true);
        mDaresView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDaresView.setItemAnimator(new DefaultItemAnimator());

        getAllDares();
    }

    private void getAllDares() {

        mDaresList.clear();
        mDaresAdapter = new DaresAdapter(mDaresList);
        mDaresView.setAdapter(mDaresAdapter);

        getDaresFromDB();
    }

    public void onViewYourProfileClick(View v) {
        Log.e(TAG, "onViewYourProfileClick >>");

        Intent showProfile = new Intent(this, UserProfileActivity.class);

        startActivity(showProfile);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart >>");
        super.onStart();

        if(mAuth.getCurrentUser() == null)
        {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy >>");

        mAuth.signOut();
        LoginManager.getInstance().logOut();
        super.onDestroy();

        Log.e(TAG, "onDestroy <<");
    }

    private void getDaresFromDB() {
        Log.e(TAG, "getDaresFromDB() >>" );

        // Read from the database
        mDatabaseDaresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e(TAG, "onDataChange() >>" );
                Dare dare;
                for (DataSnapshot dareSnapshot: dataSnapshot.getChildren()) {
                    dare = dareSnapshot.getValue(Dare.class);
                    Log.e(TAG, "#$#Dare: " + dare.toString()); // Print for debugging
                    mDaresList.add(dare);
                }

                mDaresView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        Log.e(TAG, "getDaresFromDB() <<" );
    }
}