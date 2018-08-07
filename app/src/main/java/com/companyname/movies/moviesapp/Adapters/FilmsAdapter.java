package com.companyname.movies.moviesapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.companyname.movies.moviesapp.R;
import com.companyname.movies.moviesapp.model.Film;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mohamed Ahmed on 8/6/2018.
 */

public class FilmsAdapter extends RecyclerView.Adapter<FilmsAdapter.FilmViewHolder> {
    private ArrayList<Film> mFilmArray;
    private Context mContext;

    private final FilmsAdapterClickHandeler mClickHandeler;
    public void setmFilmArray(ArrayList<Film> mFilmArray) {
        this.mFilmArray = mFilmArray;
    }

    public interface FilmsAdapterClickHandeler
    {
        void onClick(Film selectedFilm);
    }
    public FilmsAdapter(FilmsAdapterClickHandeler mClickHandeler) {

        this.mClickHandeler = mClickHandeler;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        int layoutItemId = R.layout.film_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutItemId, parent, false);
        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        holder.bind(mFilmArray.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilmArray.size();
    }

    public class FilmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mFilmImage;

        public FilmViewHolder(View itemView) {
            super(itemView);
            mFilmImage = itemView.findViewById(R.id.iv_film_item);
            itemView.setOnClickListener(this);
        }

        public void bind(Film film) {
            String imageUrl = "http://image.tmdb.org/t/p/w185/" + film.getImage();
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_file_download_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(mFilmImage);

        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Film selectedFilm = mFilmArray.get(adapterPosition);
            mClickHandeler.onClick(selectedFilm);
        }
    }
}
