package Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.deanoy.user.firebaseauthandconfig.DareDetailsActivity;
import com.deanoy.user.firebaseauthandconfig.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DareViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "DareViewHolder";
    private static final long ONE_MEGABYTE = 1024 * 1024;
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
