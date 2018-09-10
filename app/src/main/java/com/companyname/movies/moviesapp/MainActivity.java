package com.companyname.movies.moviesapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.MediaActionSound;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.companyname.movies.moviesapp.Adapters.FilmsAdapter;
import com.companyname.movies.moviesapp.model.AppDatabase;
import com.companyname.movies.moviesapp.model.Film;
import com.companyname.movies.moviesapp.utilities.JsonUtils;
import com.companyname.movies.moviesapp.utilities.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FilmsAdapter.FilmsAdapterClickHandeler, android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Film>> {

    private static final int FILM_LOADER_ID = 22;
    private static final String FILM_LOADER_EXTRA = "query";
    private static final String CURRENT_PAGE = "current_page";
    @BindView(R.id.tv_error_message_display)
    TextView mErrorTextView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBarIndicator;
    @BindView(R.id.rv_films)
    RecyclerView mFilmRecyclerView;
    FilmsAdapter mAdapter;
    String currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFilmRecyclerView.setHasFixedSize(true);
        int posterWidth = 500;
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, calculateBestSpanCount(posterWidth));
        mFilmRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new FilmsAdapter(this);

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getString(CURRENT_PAGE);
        } else {
            currentPage = "popular";
        }

        if (currentPage.equals("favorites")) {
            setupViewModel();
        } else if (isConnected()) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(FILM_LOADER_EXTRA, currentPage);
            android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
            android.support.v4.content.Loader<ArrayList<Film>> filmLoader = loaderManager.getLoader(FILM_LOADER_ID);
            if (filmLoader == null) {
                loaderManager.initLoader(FILM_LOADER_ID, queryBundle, this);
            } else
                loaderManager.restartLoader(FILM_LOADER_ID, queryBundle, this);

        } else {
            showErrorMessage();
            mErrorTextView.setText("No internet Connection");
        }
    }

    public void showLoading() {
        mProgressBarIndicator.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mFilmRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessage() {
        mProgressBarIndicator.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mFilmRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void showFilmsList() {
        mProgressBarIndicator.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mFilmRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_popular && !currentPage.equals("popular")) {
            currentPage = "popular";
            if (isConnected()) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString(FILM_LOADER_EXTRA, currentPage);
                android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                android.support.v4.content.Loader<ArrayList<Film>> filmLoader = loaderManager.getLoader(FILM_LOADER_ID);
                if (filmLoader == null) {
                    loaderManager.initLoader(FILM_LOADER_ID, queryBundle, this);
                } else
                    loaderManager.restartLoader(FILM_LOADER_ID, queryBundle, this);
            } else {
                showErrorMessage();
                mErrorTextView.setText("No internet Connection");
            }
        } else if (id == R.id.menu_top_rated && !currentPage.equals("top_rated")) {
            currentPage = "top_rated";
            if (isConnected()) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString(FILM_LOADER_EXTRA, currentPage);
                android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
                android.support.v4.content.Loader<ArrayList<Film>> filmLoader = loaderManager.getLoader(FILM_LOADER_ID);
                if (filmLoader == null) {
                    loaderManager.initLoader(FILM_LOADER_ID, queryBundle, this);
                } else
                    loaderManager.restartLoader(FILM_LOADER_ID, queryBundle, this);
            } else {
                showErrorMessage();
                mErrorTextView.setText("No internet Connection");
            }
        } else if (id == R.id.menu_favorites && !currentPage.equals("favorites")) {
            setupViewModel();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Film selectedFilm) {
        Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetail.class);
        movieDetailIntent.putExtra("selectedFilm", selectedFilm);
        startActivity(movieDetailIntent);
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_PAGE, currentPage);
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<ArrayList<Film>> onCreateLoader(int i, final Bundle bundle) {
        return new android.support.v4.content.AsyncTaskLoader<ArrayList<Film>>(this) {
            ArrayList<Film> filmsResult;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null)
                    return;
                if (filmsResult != null)
                    deliverResult(filmsResult);
                else
                    forceLoad();
                showLoading();
            }

            @Nullable
            @Override
            public ArrayList<Film> loadInBackground() {
                String requestType = bundle.getString(FILM_LOADER_EXTRA);
                Log.e("loading", requestType);
                URL weatherRequestUrl = null;
                try {
                    weatherRequestUrl = new URL(NetworkUtils.getUrl(requestType));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);
                    ArrayList<Film> filmArrayList = JsonUtils.parseFilmsJson(jsonResponse);

                    return filmArrayList;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable ArrayList<Film> data) {
                filmsResult = data;
                super.deliverResult(data);
            }
        };
    }

    public void setupViewModel() {
        currentPage = "favorites";
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFilms().observe(this, new Observer<List<Film>>() {
            @Override
            public void onChanged(@Nullable List<Film> films) {
                mAdapter.setmFilmArray(films);
                mFilmRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (currentPage.equals("favorites")) {
            getSupportLoaderManager().destroyLoader(FILM_LOADER_ID);
        } else {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(FILM_LOADER_EXTRA, currentPage);
            android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
            android.support.v4.content.Loader<ArrayList<Film>> filmLoader = loaderManager.getLoader(FILM_LOADER_ID);
            if (filmLoader == null) {
                loaderManager.initLoader(FILM_LOADER_ID, queryBundle, this);
            } else
                loaderManager.restartLoader(FILM_LOADER_ID, queryBundle, this);
        }
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<ArrayList<Film>> loader, ArrayList<Film> data) {
        if (data != null) {
            showFilmsList();
            mAdapter.setmFilmArray(data);
            mFilmRecyclerView.setAdapter(mAdapter);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<Film>> loader) {

    }

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }

    @Override
    protected void onDestroy() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (!isFinishing()) {
            LiveData<List<Film>> films = mainViewModel.getFilms();
            super.onDestroy();
            MainViewModel newViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            if (newViewModel != mainViewModel)
                newViewModel.setFilms(films);

        } else {
            super.onDestroy();
        }
    }
}
