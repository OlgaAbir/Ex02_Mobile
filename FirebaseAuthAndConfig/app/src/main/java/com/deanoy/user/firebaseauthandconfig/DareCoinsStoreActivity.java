package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.DareCoins;
import Models.DareCoinsAdapter;
public class DareCoinsStoreActivity extends Activity {

    private static final String TAG = "DareCoinsActivity";
    private RecyclerView mDareCoinsProductsView;
    private DareCoinsAdapter mDareCoinsAdapter;
    private List<DareCoins> mDareCoinsList = new ArrayList<>();

    private DatabaseReference mCoinsDatabaseRef = FirebaseDatabase.getInstance().getReference("Coins");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate() >>" );

        setContentView(R.layout.activity_dare_coins_store);
        setTitle("Dare coins Store");

        mDareCoinsProductsView = findViewById(R.id.rvDareCoinsProductsRecycler);

        mDareCoinsProductsView.setHasFixedSize(true);
        mDareCoinsProductsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDareCoinsProductsView.setItemAnimator(new DefaultItemAnimator());

        getAllCoinsProducts();
        Log.e(TAG, "onCreate() <<" );
    }

    private void getAllCoinsProducts() {
        mDareCoinsList.clear();
        mDareCoinsAdapter = new DareCoinsAdapter(mDareCoinsList);
        mDareCoinsProductsView.setAdapter(mDareCoinsAdapter);

        getDareCoinsProductsFromDB();
    }

    private void getDareCoinsProductsFromDB()
    {
        mCoinsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange() >>" );
                DareCoins dareCoins;
                mDareCoinsList.clear();
                for (DataSnapshot dareCoinsSnapshot: dataSnapshot.getChildren()) {
                    dareCoins = dareCoinsSnapshot.getValue(DareCoins.class);
                    Log.e(TAG, "Coins: " + dareCoins.toString());
                    mDareCoinsList.add(dareCoins);
                }

                mDareCoinsProductsView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
