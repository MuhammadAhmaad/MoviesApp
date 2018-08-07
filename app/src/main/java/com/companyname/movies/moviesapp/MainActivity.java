package com.companyname.movies.moviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.companyname.movies.moviesapp.Adapters.FilmsAdapter;
import com.companyname.movies.moviesapp.model.Film;
import com.companyname.movies.moviesapp.utilities.JsonUtils;
import com.companyname.movies.moviesapp.utilities.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FilmsAdapter.FilmsAdapterClickHandeler {

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
        mFilmRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new FilmsAdapter(this);
        currentPage = "popular";
        new FetchFilmsTask().execute("popular");
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
            new FetchFilmsTask().execute("popular");
        } else if (id == R.id.menu_top_rated && !currentPage.equals("top_rated")) {
            currentPage = "top_rated";
            new FetchFilmsTask().execute("top_rated");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Film selectedFilm) {
        Intent movieDetailIntent = new Intent(MainActivity.this,MovieDetail.class);
        movieDetailIntent.putExtra("selectedFilm",selectedFilm);
        startActivity(movieDetailIntent);
    }

    public class FetchFilmsTask extends AsyncTask<String, Void, ArrayList<Film>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected ArrayList<Film> doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String requestType = params[0];
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
        protected void onPostExecute(ArrayList<Film> s) {
            super.onPostExecute(s);
            if (s.size() != 0) {
                showFilmsList();
                mAdapter.setmFilmArray(s);
                mFilmRecyclerView.setAdapter(mAdapter);
            } else {
                showErrorMessage();
            }
        }
    }
}
