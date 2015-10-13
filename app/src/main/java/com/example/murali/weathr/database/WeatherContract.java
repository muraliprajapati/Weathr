package com.example.murali.weathr.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Murali on 07/10/2015.
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.example.murali.weathr.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    static class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String TABLE_NAME = "location";
        public static final String LOCATION_CODE = "location_code";
        public static final String CITY_NAME = "city_name";
        public static final String COORD_LAT = "coord_lat";
        public static final String COORD_LONG = "coord_long";

    }

    static class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String TABLE_NAME = "weather";
        public static final String LOCATION_ID = "location_id";
        public static final String DATE = "date";
        public static final String WEATHER_ID = "weather_id"; //to use icon for weather condition
        public static final String SHORT_DESCRIPTION = "short_description"; //i.e Clear, Thunderstorm
        public static final String MIN_TEMP = "min_temp";
        public static final String MAX_TEMP = "max_temp";
        public static final String HUMIDITY = "humidity";
        public static final String PRESSURE = "pressure";
        public static final String WIND_SPEED = "wind_speed";
        public static final String DEGREES = "degrees"; //metrological degrees i.e. north is 0, south is 180


    }
}
