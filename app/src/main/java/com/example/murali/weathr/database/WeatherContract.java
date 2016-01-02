package com.example.murali.weathr.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Murali on 07/10/2015.
 */
public class WeatherContract {


    public static final String CONTENT_AUTHORITY = "com.example.murali.weathr";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public static long normalizeDate(long startDate) {

        Calendar calendar = new GregorianCalendar();
        Date date = new Date(startDate);
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        date.setDate(dayOfWeek);
        return date.getTime();
    }

    public static class LocationEntry implements BaseColumns {
        //Constants for Content Provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        //Location Table Constants
        public static final String TABLE_NAME = "location";
        public static final String LOCATION_CODE = "location_code";
        public static final String CITY_NAME = "city_name";
        public static final String COORD_LAT = "coord_lat";
        public static final String COORD_LONG = "coord_long";

        public static Uri locationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }

    }

    public static class WeatherEntry implements BaseColumns {
        //Constants for Content Provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        //Location Table Constants
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

        public static Uri weatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);

        }

        public static Uri weatherWithLocationUri(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();

        }

        public static Uri weatherWithLocationAndDateUri(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(date)).build();

        }

        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }
}

