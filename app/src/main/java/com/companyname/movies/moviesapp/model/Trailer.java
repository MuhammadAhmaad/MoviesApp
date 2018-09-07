package com.companyname.movies.moviesapp.model;

/**
 * Created by Mohamed Ahmed on 9/7/2018.
 */

public class Trailer {
    private String key;

    public Trailer()
    {

    }
    public Trailer(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
