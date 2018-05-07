package Models;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deanoy.user.firebaseauthandconfig.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewViewHolder>{
    private final String TAG = "ReviewsAdapter";

    private List<Review> mReviewsList;

    public ReviewsAdapter(List<Review> reviewsList) {

        mReviewsList = reviewsList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG,"onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);

        Log.e(TAG,"onCreateViewHolder() <<");

        return new ReviewViewHolder(parent.getContext(),itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder() >> " + position);

        Review review = mReviewsList.get(position);

        holder.getReviewText().setText(review.getText());
        holder.getReviewDate().setText(review.getCreationDate().toString());
        holder.getWriterName().setText(review.getWriterName());

        Log.e(TAG,"onBindViewHolder() << "+ position);
    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }
}
