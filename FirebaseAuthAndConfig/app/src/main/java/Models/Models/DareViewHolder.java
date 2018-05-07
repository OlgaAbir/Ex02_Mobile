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

    private static String TAG = "DareViewHolder";
    private CardView mDareCardView;
    private TextView mCreatorName;
    private TextView mDareName;
    private TextView mDescription;
    private TextView mPrice;
    private ImageView mDareImg; // The image that describes the dare
    private Dare mSelectedDare;
    private Context mContext;

    public DareViewHolder(Context context, View itemView) {
        super(itemView);

        mDareCardView = itemView.findViewById(R.id.cvDare);
        mCreatorName = itemView.findViewById(R.id.tvPublisher);
        mDareName = itemView.findViewById(R.id.tvDareName);
        mDescription = itemView.findViewById(R.id.tvDareDescription);
        mPrice = itemView.findViewById(R.id.tvPrice);
        mDareImg = itemView.findViewById(R.id.ivDareImage);
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

    public TextView getDareName()
    {
        return mDareName;
    }

    public TextView getCreatorName()
    {
        return mCreatorName;
    }

    public TextView getDescription()
    {
        return mDescription;
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
