package com.companyname.movies.moviesapp.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

/**
 * Created by Mohamed Ahmed on 9/7/2018.
 */
@Database(entities = {Film.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviesapp";
    private static  AppDatabase sInstance;
    public static AppDatabase getsInstance(Context context)
    {
        if(sInstance==null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "getting  the database instance");
        return sInstance;
    }
    public abstract FilmDao filmDao();
}
