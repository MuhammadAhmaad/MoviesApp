package com.companyname.movies.moviesapp.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Mohamed Ahmed on 9/7/2018.
 */
@Dao
public interface FilmDao {
    @Query("Select * FROM films")
    List<Film> loadAllFilms();

    @Insert
    void insertFilm(Film film);

    @Delete
    void deleteFilm(Film film);
}
