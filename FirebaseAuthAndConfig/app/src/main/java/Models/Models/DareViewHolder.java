package Models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.deanoy.user.firebaseauthandconfig.DareDetailsActivity;

public class DareViewHolder extends RecyclerView.ViewHolder{

    private static String TAG = "DareViewHolder";
    private CardView mDareCardView;
    private TextView mDareName;
    private TextView mPublisherName;
    private TextView mPrice;
    private TextView mProfit;
    private TextView mReviewsCount;
    private TextView mDareDescription;
    private RatingBar mRating;
    private Dare mSelectedDare;
    private Context mContext;

    public DareViewHolder(Context context, View itemView) {
        super(itemView);

        mContext = context;

        mDareCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "CardView.onClick() >> name=" + mSelectedDare);

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

}
