package Models;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deanoy.user.firebaseauthandconfig.R;

import java.util.List;

public class DareCoinsAdapter extends RecyclerView.Adapter<DareCoinsViewHolder> {

    private final String TAG = "DareCoinsAdapter";

    private List<DareCoins> mDareCoinsList;

    public DareCoinsAdapter(List<DareCoins> dareCoinsList) {
        mDareCoinsList = dareCoinsList;
    }

    @Override
    public DareCoinsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG,"onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dare_coins, parent, false);

        Log.e(TAG,"onCreateViewHolder() <<");
        return new DareCoinsViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(DareCoinsViewHolder holder, int position) {
        Log.e(TAG,"onBindViewHolder() >> " + position);

        DareCoins dareCoinProduct = mDareCoinsList.get(position);

        // bind dare data to it's view items
        holder.setAmount(dareCoinProduct.getAmount());
        holder.setPrice(dareCoinProduct.getPrice());

        Log.e(TAG,"onBindViewHolder() << "+ position);
    }

    @Override
    public int getItemCount() {
        return mDareCoinsList.size();
    }
}
