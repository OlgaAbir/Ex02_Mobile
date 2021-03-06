package Models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.deanoy.user.firebaseauthandconfig.DareDetailsActivity;
import com.deanoy.user.firebaseauthandconfig.R;

public class DareViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "DareViewHolder";
    private CardView mDareCardView;
    private TextView mCreatorName;
    private TextView mDareName;
    private TextView mProfit;
    private TextView mPrice;
    private ImageView mDareImg;
    private Dare mSelectedDare;
    private Context mContext;

    public DareViewHolder(Context context, View itemView) {
        super(itemView);

        mDareCardView = itemView.findViewById(R.id.cvDare);
        mCreatorName = itemView.findViewById(R.id.tvPublisher);
        mDareName = itemView.findViewById(R.id.tvDareName);
        mProfit = itemView.findViewById(R.id.tvProfit);
        mPrice = itemView.findViewById(R.id.tvPrice);
        mDareImg = itemView.findViewById(R.id.ivDareImage);
        mContext = context;

        mDareCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "CardView.onClick() >> Dare =" + mSelectedDare.toString());

                AnalyticsManager.getInstance().trackDareEvents(AnalyticsManager.eDareEventType.DareSelected, mSelectedDare);
                Context context = view.getContext();
                Intent intent = new Intent(context, DareDetailsActivity.class);
                intent.putExtra("dare", mSelectedDare);
                context.startActivity(intent);
            }
        });
    }

    public Dare getSelectedDare(){ return mSelectedDare;}

    public void setSelectedDare(Dare dare)
    {
        mSelectedDare = dare;
    }

    public TextView getDareName()
    {
        return mDareName;
    }

    public TextView getCreatorName()
    {
        return mCreatorName;
    }

    public TextView getProfit()
    {
        return mProfit;
    }

    public TextView getPrice()
    {
        return mPrice;
    }

    public ImageView getDareImage()
    {
        return mDareImg;
    }
}
