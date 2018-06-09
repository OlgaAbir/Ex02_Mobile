package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import Models.AdvancedNotificationData;
import Models.AnalyticsManager;
import Models.Dare;
import Models.DareCoins;
import Models.DaresAdapter;
import Models.UserDetails;

public class AllProductsActivity extends Activity {
    private static String TAG = "AllProductsActivity";
    private static String SPINNER_UNFILTERED_OPTION = "All";
    private static String FACEBOOK_AUTH = "facebook.com";
    private static String GMAIL_AUTH = "google.com";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase ;
    private DatabaseReference mDatabaseDaresRef ;
    private RecyclerView mDaresView;
    private DaresAdapter mDaresAdapter;
    private ArrayAdapter<String> mNamesAdapter;
    private TreeSet<String> mNameSet = new TreeSet<String>();
    private ArrayList<String> mNamesArray = new ArrayList<String>();
    private List<Dare> mDaresList = new ArrayList<>();
    private ArrayList<Dare> mTempDareArray = new ArrayList<>(); // for backing up dares
    private UserDetails mUserDetails;

    // UI
    private Spinner mspnNames;
    private Button mbtnFilter;
    private EditText metMinProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);
        setTitle("All Dares");

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseDaresRef  = mDatabase.getReference("Dares");

        //UI
        mspnNames = findViewById(R.id.spnUserNames);
        mbtnFilter = findViewById(R.id.btnFilter);
        metMinProfit = findViewById(R.id.etMinProfit);
        mDaresView = findViewById(R.id.recyclerView);

        mDaresView.setHasFixedSize(true);
        mDaresView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDaresView.setItemAnimator(new DefaultItemAnimator());

        mNameSet.clear();
        mNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mNamesArray);
        mNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspnNames.setAdapter(mNamesAdapter);

        getUserDetails();
        getAllDares();
        trackSignInEvents();

        Log.e(TAG, "onCreate <<");
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart >>");
        super.onStart();

        if(mAuth.getCurrentUser() == null)
        {
            finish();
        }

        Log.e(TAG, "onStart <<");
    }

    private void getAllDares() {
        mDaresList.clear();
        mDaresAdapter = new DaresAdapter(mDaresList);
        mDaresView.setAdapter(mDaresAdapter);

        getDaresFromDB();
    }

    // onClicks
    public void onViewYourProfileClick(View v) {
        Log.e(TAG, "onViewYourProfileClick >>");

        Intent showProfile = new Intent(this, UserProfileActivity.class);

        startActivity(showProfile);
    }

    public void onFilterClick(View v) {

        Log.e(TAG, "onFilterCLick >>" );

        ArrayList<Dare> filteredDaresArray = new ArrayList<>();
        int minProfit = 0;
        String selectedName = (String)mspnNames.getSelectedItem();

        Log.e(TAG, "filtering by " + selectedName );
        boolean shouldFilterByName = !selectedName.equals(SPINNER_UNFILTERED_OPTION);
        boolean shouldFilterByProfit = !metMinProfit.getText().toString().isEmpty();

        if(shouldFilterByName) {
            Log.e(TAG, "filtering by Name");
        }

        // Reset for re-filtering
        mDaresList.clear();
        mDaresList.addAll(mTempDareArray);

        if(shouldFilterByProfit) {
            minProfit = Integer.parseInt(metMinProfit.getText().toString());
        }

        trackFilterEvent(shouldFilterByProfit, minProfit, shouldFilterByName, selectedName);
        // Filter
        for(Dare dare: mDaresList) {
            if((dare.getProfit() >= minProfit) && (!shouldFilterByName || selectedName.equals(dare.getCreaterName()))) {
                filteredDaresArray.add(dare);
                Log.e(TAG, "added to array" );
            }
        }

        mDaresList.clear();
        mDaresList.addAll(filteredDaresArray);
        mDaresView.getAdapter().notifyDataSetChanged();
        Log.e(TAG, "onFilterCLick <<" );
    }

    public void onProfitSortClick(View v) {
        mDaresList.sort(new Comparator<Dare>() {
            @Override
            public int compare(Dare dare, Dare t1) {
                double result = dare.getProfit() - t1.getProfit();
                if(result > 0) {
                    return -1;
                } else if (result < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        mDaresView.getAdapter().notifyDataSetChanged();
        AnalyticsManager.getInstance().trackSortingMethod("Profit");
    }

    public void onDescriptionSortClick(View v) {
        mDaresList.sort(new Comparator<Dare>() {
            @Override
            public int compare(Dare dare, Dare t1) {
                return t1.getDescription().compareTo(dare.getDescription());
            }
        });
        mDaresView.getAdapter().notifyDataSetChanged();
        AnalyticsManager.getInstance().trackSortingMethod("Description");
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
                int userDaresCounter = 0;
                for (DataSnapshot dareSnapshot: dataSnapshot.getChildren()) {
                    dare = dareSnapshot.getValue(Dare.class);
                    dare.setDareId(dareSnapshot.getKey());
                    Log.e(TAG, "Dare: " + dare.toString());
                    mDaresList.add(dare);
                    mNameSet.add(dare.getCreaterName());

                    if(mAuth.getCurrentUser() != null) {
                        if (dare.getCreaterID().contentEquals(mAuth.getCurrentUser().getUid())) {
                            userDaresCounter++;
                        }
                    }
                }

                mTempDareArray.addAll(mDaresList);
                mDaresView.getAdapter().notifyDataSetChanged();
                mNamesArray.addAll(mNameSet);
                mspnNames.setAdapter(mNamesAdapter);
                AnalyticsManager.getInstance().setUserProperty(getApplicationContext().getString(R.string.dares_created_count), Integer.toString(userDaresCounter));

                if(AdvancedNotificationData.getInstance().getFilterDares() != null &&
                        !AdvancedNotificationData.getInstance().getFilterDares().isEmpty())
                {
                    int position = mNamesAdapter.getPosition(AdvancedNotificationData.getInstance().getFilterDares());
                    mspnNames.setSelection(position);
                    onFilterClick(mbtnFilter);
                    AdvancedNotificationData.getInstance().setFilterDares("");
                }

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
                setUserProperties();
                Log.e(TAG, "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    // Analytics

    private void setUserProperties() {
        AnalyticsManager.getInstance().setUserProperty(getApplicationContext().getString(R.string.dares_completed_count), Integer.toString(mUserDetails.getCompletedDareIds().size()));
        AnalyticsManager.getInstance().setUserProperty(getApplicationContext().getString(R.string.dares_purchased_count), Integer.toString(mUserDetails.getPurchasedDareIds().size()));
        AnalyticsManager.getInstance().setUserProperty(getApplicationContext().getString(R.string.balance), Integer.toString(mUserDetails.getBalance()));
    }

    private void trackSignInEvents() {
        FirebaseUser user = mAuth.getCurrentUser();
        AnalyticsManager.getInstance().setUserID(user.getUid());
        String signInMethod;

        if(user.isAnonymous()) {
            signInMethod = "Anonymous";
        } else if(isUsingAuthMethod(GMAIL_AUTH)) {
            signInMethod = GMAIL_AUTH;
        } else if(isUsingAuthMethod(FACEBOOK_AUTH)) {
            signInMethod = FACEBOOK_AUTH;
        } else {
            signInMethod = "email/pass";
        }

        AnalyticsManager.getInstance().setUserProperty(getApplicationContext().getString(R.string.user_name), user.getDisplayName());
        AnalyticsManager.getInstance().trackSignInEvent(signInMethod);
    }

    private void trackFilterEvent(boolean isProfitFiltered, int profit, boolean isNameFiltered, String selectedName) {
        if (!isProfitFiltered && !isNameFiltered) {
            return; // No filter was selected, nothing to track.
        }

        String filterMethod;
        String value;

        if(isProfitFiltered && isNameFiltered) {
            filterMethod = "Name and Profit";
            value = String.format("Name: %s, Min profit: %d", selectedName, profit);
        } else if (isProfitFiltered) {
            filterMethod = "Profit";
            value = Integer.toString(profit);
        } else {
            filterMethod = "Name";
            value = selectedName;
        }

        AnalyticsManager.getInstance().trackFilterMethod(filterMethod, value);
    }

    // Check if the user is logged in using the auth type in the parameter
    private boolean isUsingAuthMethod(String authType) {
        boolean isUsingAuthMethod = false;

        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals(authType)) {
                Log.e(TAG, "User is signed in with " + authType);
                isUsingAuthMethod = true;
            }
        }

        return isUsingAuthMethod;
    }
}