package com.companyname.movies.moviesapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.companyname.movies.moviesapp.R;
import com.companyname.movies.moviesapp.model.Film;
import com.companyname.movies.moviesapp.model.Trailer;

import java.util.ArrayList;

/**
 * Created by Mohamed Ahmed on 9/7/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    ArrayList<Trailer> trailerArrayList;
    private Context mContext;

    public void setmTrailerArray(ArrayList<Trailer> mTrailers) {
        this.trailerArrayList= mTrailers;
    }

    public interface TrailerAdapterClickHandeler
    {
        void onClick(Trailer selectedTrailer);
    }
    private final TrailerAdapterClickHandeler mClickHandeler;
    public TrailerAdapter(TrailerAdapterClickHandeler mClickHandeler)
    {
        this.mClickHandeler = mClickHandeler;
    }
    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        int layoutItemId = R.layout.trailer_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutItemId, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
       String filmNo = position+1+"";
        holder.mTitle.setText("Trailer "+filmNo);
    }

    @Override
    public int getItemCount() {
        return trailerArrayList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.trailer_title);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer selectedTrailer = trailerArrayList.get(adapterPosition);
            mClickHandeler.onClick(selectedTrailer);
        }
    }
}
