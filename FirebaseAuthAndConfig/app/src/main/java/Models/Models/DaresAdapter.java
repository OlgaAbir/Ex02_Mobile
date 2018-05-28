package Models;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deanoy.user.firebaseauthandconfig.R;

import java.util.List;

public class DaresAdapter extends RecyclerView.Adapter<DareViewHolder> {

    private final String TAG = "DaresAdapter";

    private List<Dare> mDaresList;

    public DaresAdapter(List<Dare> daresList) {
        mDaresList = daresList;
    }

    @Override
    public DareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG,"onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dare, parent, false);

        Log.e(TAG,"onCreateViewHolder() <<");
        return new DareViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(DareViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder() >> " + position);

        Dare dare = mDaresList.get(position);

        // bind dare data to it's view items
        holder.setSelectedDare(dare);
        holder.getDareName().setText("Name: " + dare.getDareName());
        holder.getCreatorName().setText("Publisher: " + dare.getCreaterName());
        String price = "Price: " + dare.getBuyInCost();

        if(AdvancedNotificationData.getInstance().getSale() > 0)
        {
            price += " -" + AdvancedNotificationData.getInstance().getSale() + "%";
        }

        holder.getPrice().setText(price);
        holder.getProfit().setText("Profit: " + dare.getProfit());

        Log.e(TAG,"onBindViewHolder() << "+ position);
    }

    @Override
    public int getItemCount() {
        return mDaresList.size();
    }
}
