package Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.deanoy.user.firebaseauthandconfig.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {

    private TextView mReviewText;
    private TextView mReviewDate;
    private TextView mWriterName;

    public ReviewViewHolder(Context context, View view) {

        super(view);

        mReviewText = view.findViewById(R.id.tvReviewText);
        mReviewDate = view.findViewById(R.id.tvDate);
        mWriterName = view.findViewById(R.id.tvWriterName);
    }

    public TextView getReviewText()
    {
        return mReviewText;
    }

    public TextView getReviewDate()
    {
        return mReviewDate;
    }

    public TextView getWriterName()
    {
        return mWriterName;
    }
}
