package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.BillingManager;
import Models.DareCoins;
import Models.DareCoinsAdapter;
import Models.UserDetails;

public class DareCoinsStoreActivity extends Activity implements BillingManager.BillingUpdatesListener {

    private static final String TAG = "DareCoinsActivity";
    private static String USER_DETAILS_DATA = "user_details";

    public final static String _4NIS_CREDIT = "nis_credit_4";
    public final static String _8NIS_CREDIT = "8nis_credit";
    public final static String _12NIS_CREDIT = "nis_credit_12";

    private static BillingManager mBillingManager;
    private RecyclerView mDareCoinsProductsView;
    private DareCoinsAdapter mDareCoinsAdapter;
    private List<DareCoins> mDareCoinsList = new ArrayList<>();
    private UserDetails mUserDetails;

    private DatabaseReference mCoinsDatabaseRef = FirebaseDatabase.getInstance().getReference("Coins");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate() >>" );

        setContentView(R.layout.activity_dare_coins_store);
        setTitle("Dare coins Store");

        mBillingManager = new BillingManager(this,this);
        mDareCoinsProductsView = findViewById(R.id.rvDareCoinsProductsRecycler);

        mDareCoinsProductsView.setHasFixedSize(true);
        mDareCoinsProductsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDareCoinsProductsView.setItemAnimator(new DefaultItemAnimator());

        // Get user details
        Bundle extras = getIntent().getExtras();
        mUserDetails = extras.getParcelable(USER_DETAILS_DATA);

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

    public void onBillingClientSetupFinished() {

        Log.e(TAG,"onBillingSetupFinished() >>");

        Log.e(TAG,"onBillingSetupFinished() <<");

    }

    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {

        Log.e(TAG,"onConsumeFinished() >> result:"+result+" ,token:"+token);

        if (result == BillingClient.BillingResponse.OK) {
            Log.e(TAG, "Product with token:"+ token+ " was consumed successfully");
        } else {
            Log.e(TAG, "Unable to purchase Product with token:" + token + ". Error code: " + result);
        }

        Log.e(TAG,"onConsumeFinished() <<");

    }

    public void onPurchasesUpdated(int resultCode, List<Purchase> purchases){

        Log.e(TAG,"onPurchasesUpdated() >> ");

        if (resultCode != BillingClient.BillingResponse.OK) {
            Log.e(TAG,"onPurchasesUpdated() << Error:"+resultCode);
            return;
        }

        for (Purchase purchase : purchases) {
            Log.e(TAG, "onPurchasesUpdated() >> " + purchase.toString());

            if (purchase.getSku().contains("credit")) {
                Log.e(TAG, "onPurchasesUpdated() >> consuming " + purchase.getSku());
                //Only consume  one time product (subscription can't be consumed).
                mBillingManager.consumeAsync(purchase.getPurchaseToken());
                updateBalance(purchase.getSku());
                recordTransaction(purchase);
            }

        }

        Log.e(TAG,"onPurchasesUpdated() <<");
    }

    private void recordTransaction(Purchase purchase) {
        Log.e(TAG,"onPurchasesUpdated() >> for purchase " + purchase);

        DatabaseReference purchaseTransactionRef = FirebaseDatabase.getInstance().getReference("Purchase").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String purchaseKey = purchaseTransactionRef.push().getKey();
        purchaseTransactionRef.child(purchaseKey).child("time_of_purchase").setValue(purchase.getPurchaseTime());
        purchaseTransactionRef.child(purchaseKey).child("sku").setValue(purchase.getSku());
        purchaseTransactionRef.child(purchaseKey).child("order_id").setValue(purchase.getOrderId());
        purchaseTransactionRef.child(purchaseKey).child("purchase_token").setValue(purchase.getPurchaseToken());
        purchaseTransactionRef.child(purchaseKey).child("amount").setValue(1);

        Log.e(TAG,"onPurchasesUpdated() <<");
    }

    public static void BuyCoins(String productPrice) {

        Log.e(TAG, "productPrice: " + productPrice);
        String product;
        String sku = BillingClient.SkuType.INAPP;

        if(productPrice.equals("Price: 4 NIS"))
        {
            product = _4NIS_CREDIT;
        }
        else if(productPrice.equals("Price: 8 NIS"))
        {
            product = _8NIS_CREDIT;
        }
        else
        {
            product = _12NIS_CREDIT;
        }

        mBillingManager.initiatePurchaseFlow(product,sku);
    }

    private void updateBalance(String orderId) {
        int balanceToAdd;

        if(orderId.equals(_4NIS_CREDIT))
        {
            balanceToAdd = 50;
        }
        else if(orderId.equals(_8NIS_CREDIT))
        {
            balanceToAdd = 120;
        }
        else
        {
            balanceToAdd = 200;
        }

        Log.e(TAG,"updateBalance() >> Adding " + balanceToAdd + "credits to user");

        mUserDetails.setBalance(mUserDetails.getBalance() + balanceToAdd);
        DatabaseReference userDetailsDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDetailsDatabaseRef.setValue(mUserDetails);
    }
}
