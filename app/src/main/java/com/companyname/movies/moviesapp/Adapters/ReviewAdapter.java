package com.companyname.movies.moviesapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.companyname.movies.moviesapp.R;
import com.companyname.movies.moviesapp.model.Review;

import java.util.ArrayList;

/**
 * Created by Mohamed Ahmed on 9/7/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<Review> reviewsList;

    public void setReviewsList(ArrayList<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    Context mContext;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutItemID= R.layout.review_item_layout;
        LayoutInflater layoutInflater= LayoutInflater.from(mContext);
        View view =layoutInflater.inflate(layoutItemID,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviewsList.get(position));
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView authorTv;
        TextView contentTv;
        public ReviewViewHolder(View itemView)
        {
            super(itemView);
            authorTv = itemView.findViewById(R.id.author_tv);
            contentTv =itemView.findViewById(R.id.content_tv);
        }
        public void bind(Review review)
        {
            authorTv.setText(review.getAuthor());
            contentTv.setText(review.getContent());
        }
    }
}
