package com.companyname.movies.moviesapp.model;

import java.io.Serializable;

/**
 * Created by Mohamed Ahmed on 8/6/2018.
 */

public class Film implements Serializable {
    private int id;
    private String title;
    private String image;
    private String overview;
    private String releaseDate;
    private String rating;

    public Film()
    {

    }
    public Film(int id, String title, String image, String overview, String releaseDate, String rating) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
