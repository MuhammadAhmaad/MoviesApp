package com.companyname.movies.moviesapp.utilities;

import android.util.Log;

import com.companyname.movies.moviesapp.model.Film;
import com.companyname.movies.moviesapp.model.Review;
import com.companyname.movies.moviesapp.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mohamed Ahmed on 8/6/2018.
 */

public class JsonUtils {
    public static ArrayList<Film> parseFilmsJson(String jsonFilms) {
        final String ID = "id";
        final String RATING = "vote_average";
        final String TITLE = "title";
        final String IMAGE = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String RESULTS = "results";
        Film film;
        JSONObject jsonFilmsObject;
        ArrayList<Film> filmArrayList = null;

        try {
            jsonFilmsObject = new JSONObject(jsonFilms);
            JSONArray jsonFilmsArray = jsonFilmsObject.getJSONArray(RESULTS);
            filmArrayList = new ArrayList<>();
            for (int i = 0; i < jsonFilmsArray.length(); i++) {
                JSONObject jsonFilmObject = jsonFilmsArray.getJSONObject(i);
                film = new Film();
                film.setId(jsonFilmObject.getInt(ID));
                film.setImage(jsonFilmObject.getString(IMAGE));
                film.setOverview(jsonFilmObject.getString(OVERVIEW));
                film.setTitle(jsonFilmObject.getString(TITLE));
                film.setReleaseDate(jsonFilmObject.getString(RELEASE_DATE));
                String rate = jsonFilmObject.get(RATING)+" ";
                film.setRating(rate);
                filmArrayList.add(film);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return filmArrayList;

    }

    public static ArrayList<Review> parseReviewFilm(String jsonResponse) {
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String RESULTS = "results";
        Review review;
        JSONObject jsonReviewObject;
        ArrayList<Review>reviewArrayList=null;

        try {
            jsonReviewObject = new JSONObject(jsonResponse);
            JSONArray jsonReviewsArray = jsonReviewObject.getJSONArray(RESULTS);
            reviewArrayList = new ArrayList<>();
            for (int i=0;i<jsonReviewsArray.length();i++)
            {
                JSONObject jsonObject = jsonReviewsArray.getJSONObject(i);
                review = new Review();
                review.setAuthor(jsonObject.getString(AUTHOR));
                review.setContent(jsonObject.getString(CONTENT));
                reviewArrayList.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewArrayList;
    }

    public static ArrayList<Trailer> parseTrailersJson(String jsonResponse) {
        final String KEY = "key";
        final String RESULTS = "results";
        Trailer trailer;
        JSONObject jsonTrailerObject;
        ArrayList<Trailer>trailerArrayList=null;

        try {
            jsonTrailerObject = new JSONObject(jsonResponse);
            JSONArray jsonTrailersArray = jsonTrailerObject.getJSONArray(RESULTS);
            trailerArrayList = new ArrayList<>();
            for(int i =0;i<jsonTrailersArray.length();i++)
            {
                JSONObject jsonObject = jsonTrailersArray.getJSONObject(i);
                trailer=new Trailer();
                trailer.setKey(jsonObject.getString(KEY));
                trailerArrayList.add(trailer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerArrayList;
    }
}
