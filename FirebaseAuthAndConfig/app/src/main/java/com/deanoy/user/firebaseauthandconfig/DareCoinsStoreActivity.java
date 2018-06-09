package com.deanoy.user.firebaseauthandconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Models.Dare;
import Models.DareCoins;
import Models.DareCoinsAdapter;
import Models.DaresAdapter;

public class DareCoinsStoreActivity extends Activity {

    private RecyclerView mDareCoinsProductsView;
    private DareCoinsAdapter mDareCoinsAdapter;
    private List<DareCoins> mDareCoinsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dare_coins_store);
        setTitle("Dare coins Store");

        mDareCoinsProductsView = findViewById(R.id.rvDareCoinsProductsRecycler);

        mDareCoinsProductsView.setHasFixedSize(true);
        mDareCoinsProductsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDareCoinsProductsView.setItemAnimator(new DefaultItemAnimator());

        getAllCoinsProducts();
    }

    private void getAllCoinsProducts() {
        mDareCoinsList.clear();
        mDareCoinsAdapter = new DareCoinsAdapter(mDareCoinsList);
        mDareCoinsProductsView.setAdapter(mDareCoinsAdapter);

        getDareCoinsFromDB();
    }

    private void getDareCoinsFromDB()
    {

    }
}
