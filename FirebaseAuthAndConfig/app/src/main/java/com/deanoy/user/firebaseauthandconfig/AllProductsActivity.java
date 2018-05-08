package com.deanoy.user.firebaseauthandconfig;

import com.deanoy.user.firebaseauthandconfig.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import Models.Dare;
import Models.DaresAdapter;

import static java.lang.Thread.sleep;

public class AllProductsActivity extends Activity {
    private static String TAG = "AllProductsActivity";
    private static String SPINNER_UNFILTERED_OPTION = "All";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase ;
    private DatabaseReference mDatabaseDaresRef ;
    private RecyclerView mDaresView;
    private DaresAdapter mDaresAdapter;
    private ArrayAdapter<String> mNamesAdapter;
    private TreeSet<String> mNameSet = new TreeSet<String>();
    private ArrayList<String> mNamesArray = new ArrayList<String>();
    private List<Dare> mDaresList = new ArrayList<>();

    // UI
    private Spinner mspnNames;
    private EditText metMinProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        Log.e(TAG, "onCreate >>");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseDaresRef  = mDatabase.getReference("Dares");

        //UI
        mspnNames = findViewById(R.id.spnUserNames);
        mspnNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemSelected >> " + adapterView.getContext());
                Toast.makeText(adapterView.getContext(), "Selected item: " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onItemSelected << ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mDaresView = findViewById(R.id.recyclerView);

        mDaresView.setHasFixedSize(true);
        mDaresView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDaresView.setItemAnimator(new DefaultItemAnimator());

        mNameSet.clear();
        mNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mNamesArray);
        mNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspnNames.setAdapter(mNamesAdapter);

        getAllDares();
        Log.e(TAG, "onCreate <<");
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

    private void getDaresFromDB() {
        Log.e(TAG, "getDaresFromDB() >>" );

        // Read from the database
        mDatabaseDaresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e(TAG, "onDataChange() >>" );
                mDaresList.clear();
                mNamesArray.clear();
                mNamesArray.add(SPINNER_UNFILTERED_OPTION);
                mNameSet.clear();
                Dare dare;
                for (DataSnapshot dareSnapshot: dataSnapshot.getChildren()) {
                    dare = dareSnapshot.getValue(Dare.class);
                    dare.setDareId(dareSnapshot.getKey());
                    Log.e(TAG, "#$#Dare: " + dare.toString()); // Print for debugging TODO: remove before assigning
                    mDaresList.add(dare);
                    mNameSet.add(dare.getCreaterName());
                }

                mDaresView.getAdapter().notifyDataSetChanged();
                mNamesArray.addAll(mNameSet);
                mspnNames.setAdapter(mNamesAdapter);
                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
        Log.e(TAG, "getDaresFromDB() <<" );
    }


}