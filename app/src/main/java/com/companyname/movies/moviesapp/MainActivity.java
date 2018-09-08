package com.companyname.movies.moviesapp;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    @BindView(R.id.tv_error_message_display)
    TextView mErrorTextView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBarIndicator;
    @BindView(R.id.rv_films)
    RecyclerView mFilmRecyclerView;
    FilmsAdapter mAdapter;
    String currentPage;
    private AppDatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFilmRecyclerView.setHasFixedSize(true);
        mFilmRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new FilmsAdapter(this);
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
        mDB = AppDatabase.getsInstance(this);
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
            reteriveFavorites();
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

    public void reteriveFavorites() {
        currentPage = "favorites";
        final LiveData<List<Film>> filmsArrayList = mDB.filmDao().loadAllFilms();
        filmsArrayList.observe(this, new Observer<List<Film>>() {
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
        Log.e("currrrnnnnnn", currentPage);
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


}
