package com.companyname.movies.moviesapp.utilities;

import android.util.Log;

import com.companyname.movies.moviesapp.model.Film;

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
}
