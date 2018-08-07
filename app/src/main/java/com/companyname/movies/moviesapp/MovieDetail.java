package com.companyname.movies.moviesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.companyname.movies.moviesapp.model.Film;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity {

    @BindView(R.id.tv_movie_title) TextView mMovieTitle;
    @BindView(R.id.tv_relesae_date) TextView mMovieReleaseDate;
    @BindView(R.id.tv_vote_average)TextView mMovieRating;
    @BindView(R.id.tv_overview)TextView mMovieOverView;
    @BindView(R.id.iv_movie_image)ImageView mMovieImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Film selectedFilm = (Film)getIntent().getSerializableExtra("selectedFilm");
        getSupportActionBar().setTitle("MovieDetail");
        mMovieTitle.setText(selectedFilm.getTitle());
        mMovieRating.setText(selectedFilm.getRating()+"/10");
        mMovieReleaseDate.setText(selectedFilm.getReleaseDate());
        String imageUrl = "http://image.tmdb.org/t/p/w185/" + selectedFilm.getImage();
        mMovieOverView.setText(selectedFilm.getOverview());
        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_file_download_black_24dp)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(mMovieImage);
    }
}
