package com.companyname.movies.moviesapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.companyname.movies.moviesapp.model.AppDatabase;
import com.companyname.movies.moviesapp.model.Film;

import java.util.List;

/**
 * Created by Mohamed Ahmed on 9/10/2018.
 */

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Film>> films;

    public void setFilms(LiveData<List<Film>> films) {
        this.films = films;
    }

    public LiveData<List<Film>> getFilms() {
        return films;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getsInstance(this.getApplication());
        films = appDatabase.filmDao().loadAllFilms();
    }
}
