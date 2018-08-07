package com.companyname.movies.moviesapp.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mohamed Ahmed on 8/6/2018.
 */

public class NetworkUtils {
    private static final String API_KEY = "YOUR API KEY";
    private static final String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    private static final String TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    public static String getUrl(String requestType) {
        String url = null;
        if (requestType.equals("popular"))
            url= POPULAR_URL;
        else if(requestType.equals("top_rated"))
            url =  TOP_RATED_URL;
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
