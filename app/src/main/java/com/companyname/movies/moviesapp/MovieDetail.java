package com.companyname.movies.moviesapp;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.companyname.movies.moviesapp.Adapters.ReviewAdapter;
import com.companyname.movies.moviesapp.Adapters.TrailerAdapter;
import com.companyname.movies.moviesapp.model.AppDatabase;
import com.companyname.movies.moviesapp.model.Film;
import com.companyname.movies.moviesapp.model.Review;
import com.companyname.movies.moviesapp.model.Trailer;
import com.companyname.movies.moviesapp.utilities.JsonUtils;
import com.companyname.movies.moviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Review>>,TrailerAdapter.TrailerAdapterClickHandeler {

    @BindView(R.id.tv_movie_title) TextView mMovieTitle;
    @BindView(R.id.tv_relesae_date) TextView mMovieReleaseDate;
    @BindView(R.id.tv_vote_average)TextView mMovieRating;
    @BindView(R.id.tv_overview)TextView mMovieOverView;
    @BindView(R.id.iv_movie_image)ImageView mMovieImage;
    @BindView(R.id.reviews_rv) RecyclerView reviewsRecyclerView;
    @BindView(R.id.trailers_rv) RecyclerView trailersRecyclerView;
    @BindView(R.id.favorite_button) ImageButton favoriteButton;
    boolean isFavorite =false;
    ReviewAdapter mAdapter;
    TrailerAdapter mTrailerAdapter;
    private static final String FILM_ID="film_id";
    private static final int REVIEW_LOADER_ID = 55;
    private AppDatabase mDB;
    public Film selectedFilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedFilm = (Film)getIntent().getSerializableExtra("selectedFilm");
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

        mDB = AppDatabase.getsInstance(this);

        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReviewAdapter();

        trailersRecyclerView.setHasFixedSize(true);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailerAdapter = new TrailerAdapter(this);

        Bundle queryBundle = new Bundle();
        queryBundle.putInt(FILM_ID,selectedFilm.getId());
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<ArrayList<Film>> filmLoader = loaderManager.getLoader(REVIEW_LOADER_ID);
        if(filmLoader==null) {
            loaderManager.initLoader(REVIEW_LOADER_ID, queryBundle, this);
        }
        else
        {
            loaderManager.restartLoader(REVIEW_LOADER_ID,queryBundle,this);
        }
        new GetTrailers().execute(selectedFilm.getId()+"");
        trailersRecyclerView.setFocusable(false);

        checkIfFavorite();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void checkIfFavorite() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFilms().observe(this, new Observer<List<Film>>() {
            @Override
            public void onChanged(@Nullable List<Film> films) {
                for(Film f:films){
                    if(f.getId()==selectedFilm.getId())
                    {
                        isFavorite = true;
                        favoriteButton.setBackgroundResource(R.drawable.ic_star_black_24dp);
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Review>>(this) {
            ArrayList<Review>reviews;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args==null)
                    return;
                if(reviews!=null)
                    deliverResult(reviews);
                else
                    forceLoad();
            }

            @Nullable
            @Override
            public ArrayList<Review> loadInBackground() {
                String stringUrl = "http://api.themoviedb.org/3/movie/"+args.getInt(FILM_ID)+"/reviews?api_key=638b367b3b8d3459f13f9116f26a019f";
                URL url=null;
                try {
                    url = new URL(stringUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                    ArrayList<Review> reviewsList = JsonUtils.parseReviewFilm(jsonResponse);
                    return reviewsList;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(@Nullable ArrayList<Review> data) {
                reviews=data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
        mAdapter.setReviewsList(data);
        reviewsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Review>> loader) {

    }

    @Override
    public void onClick(Trailer selectedTrailer) {
        String key = selectedTrailer.getKey();
        String strinUrl = "https://www.youtube.com/watch?v="+key;
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public void onFavoriteClicked(View view) {
        if(isFavorite)
        {
            isFavorite=false;
            favoriteButton.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDB.filmDao().deleteFilm(selectedFilm);
                }
            });
        }
        else {
            isFavorite=true;
            favoriteButton.setBackgroundResource(R.drawable.ic_star_black_24dp);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDB.filmDao().insertFilm(selectedFilm);
                }
            });
        }


    }

    public class GetTrailers extends AsyncTask<String,Void,ArrayList<Trailer>>
    {


        @Override
        protected ArrayList<Trailer> doInBackground(String... strings) {
            if(strings.length==0)
                return null;
            String filmID = strings[0];
            String stringUrl = "http://api.themoviedb.org/3/movie/"+filmID+"/videos?api_key=638b367b3b8d3459f13f9116f26a019f";
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(url);
                ArrayList<Trailer> trailerArrayList = JsonUtils.parseTrailersJson(jsonResponse);
                return trailerArrayList;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            super.onPostExecute(trailers);
            mTrailerAdapter.setmTrailerArray(trailers);
            trailersRecyclerView.setAdapter(mTrailerAdapter);
        }
    }
}
