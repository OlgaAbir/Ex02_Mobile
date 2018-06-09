package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.BillingManager;
import Models.DareCoins;
import Models.DareCoinsAdapter;
public class DareCoinsStoreActivity extends Activity implements BillingManager.BillingUpdatesListener {

    private static final String TAG = "DareCoinsActivity";

    public final static String _1NIS_CREDIT = "1nis_credit";
    public final static String _2NIS_CREDIT = "2nis_credit";
    public final static String _3NIS_CREDIT = "3nis_credit";

    private static BillingManager mBillingManager;
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

        mBillingManager = new BillingManager(this,this);
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

    public void onBillingClientSetupFinished() {

        Log.e(TAG,"onBillingSetupFinished() >>");

        Log.e(TAG,"onBillingSetupFinished() <<");

    }

    public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {

        Log.e(TAG,"onConsumeFinished() >> result:"+result+" ,token:"+token);


        if (result == BillingClient.BillingResponse.OK) {
            displayMessage("Product with token:"+ token+ " was consumed successfully");
        } else {
            displayMessage("Error consuming product with token:" + token + " , error code:"+result);
        }

        Log.e(TAG,"onConsumeFinished() <<");

    }

    public void onPurchasesUpdated(int resultCode,List<Purchase> purchases){

        Log.e(TAG,"onPurchasesUpdated() >> ");

        if (resultCode != BillingClient.BillingResponse.OK) {
            Log.e(TAG,"onPurchasesUpdated() << Error:"+resultCode);
            return;
        }

        for (Purchase purchase : purchases) {
            Log.e(TAG, "onPurchasesUpdated() >> " + purchase.toString());

            displayMessage("onPurchasesUpdated() >> " + purchase.getSku());

            if (purchase.getSku().contains("credit")) {
                Log.e(TAG, "onPurchasesUpdated() >> consuming " + purchase.getSku());
                //Only consume  one time product (subscription can't be consumed).
                mBillingManager.consumeAsync(purchase.getPurchaseToken());
            }
            //Update the server...
        }

        Log.e(TAG,"onPurchasesUpdated() <<");
    }

    public static void BuyCoins(String productPrice) {

        Log.e(TAG, "productPrice: " + productPrice);
        String product;
        String sku = BillingClient.SkuType.INAPP;

        if(productPrice.equals("Price: 1 NIS"))
        {
            product = _1NIS_CREDIT;
        }
        else if(productPrice.equals("Price: 2 NIS"))
        {
            product = _2NIS_CREDIT;
        }
        else
        {
            product = _3NIS_CREDIT;
        }

        mBillingManager.initiatePurchaseFlow(product,sku);
    }

    public void displayMessage(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }
}
