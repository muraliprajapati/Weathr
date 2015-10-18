package com.example.murali.weathr.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Murali on 17/10/2015.
 */
public class WeatherProvider extends ContentProvider {
    public static final int WEATHER = 200;                                  //DIR
    public static final int WEATHER_WITH_LOCATION = 201;                    //DIR
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 202;           //ITEM
    public static final int LOCATION = 300;                                //DIR

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = WeatherContract.CONTENT_AUTHORITY;
        String weatherPath = WeatherContract.PATH_WEATHER;
        String locationPath = WeatherContract.PATH_LOCATION;
        uriMatcher.addURI(authority, weatherPath, WEATHER);
        uriMatcher.addURI(authority, weatherPath + "/*", WEATHER_WITH_LOCATION);
        uriMatcher.addURI(authority, weatherPath + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        uriMatcher.addURI(authority, locationPath, LOCATION);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        WeatherDatabase database = new WeatherDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
