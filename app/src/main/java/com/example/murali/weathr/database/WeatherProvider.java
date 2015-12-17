package com.example.murali.weathr.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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

    public static final SQLiteQueryBuilder weatherWithLocationQueryBuilder;

    static {

        weatherWithLocationQueryBuilder = new SQLiteQueryBuilder();


        /*
        *   JOIN between weather  and location table to get data as per location setting
        *   sqlite INNER JOIN statement:
        *   weather INNER JOIN location ON weather.location_id = location._id
        *   SQLiteQueryBuilder.setTables method sets the list of tables to query.
        *
         */

        String joinSyntax = WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " + WeatherContract.LocationEntry.TABLE_NAME + " ON " + WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.LOCATION_ID + " = " + WeatherContract.LocationEntry.TABLE_NAME + "." + WeatherContract.LocationEntry._ID;
        weatherWithLocationQueryBuilder.setTables(joinSyntax);

    }

    WeatherDatabaseHelper dbHelper;
    UriMatcher uriMatcher = buildUriMatcher();


    /*
    *   To get weather data for specified location in SharedPreferences we need Selection statement
    *   SQLite selection statement: location.location_code = ?
    *   Here the ? will be replaced by SelectionArgs in query method.

     */
    String locationSelection = WeatherContract.LocationEntry.TABLE_NAME + "." + WeatherContract.LocationEntry.LOCATION_CODE + " = ?";


        /*
        *   To get weather data for specified location in SharedPreferences and date we need Selection statement
        *   SQLite selection statement: location.location_setting = ? AND date = ?
        *   Here the ? will be replaced by SelectionArgs in query method.

         */

    String locationAndDateSelection = WeatherContract.LocationEntry.TABLE_NAME + "." + WeatherContract.LocationEntry.LOCATION_CODE + " = ? AND " + WeatherContract.WeatherEntry.DATE + " = ?";

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = WeatherContract.CONTENT_AUTHORITY;
        String weatherPath = WeatherContract.PATH_WEATHER;
        String locationPath = WeatherContract.PATH_LOCATION;
        uriMatcher.addURI(authority, weatherPath, WEATHER);
        uriMatcher.addURI(authority, weatherPath + "/*", WEATHER_WITH_LOCATION);                // " * " is used for string
        uriMatcher.addURI(authority, weatherPath + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);     // " # " is used for number
        uriMatcher.addURI(authority, locationPath, LOCATION);
        return uriMatcher;
    }

    private Cursor getWeatherWithLocation(Uri uri, String[] projection, String sortOrder) {
        String location = WeatherContract.WeatherEntry.getLocationFromUri(uri);

        String selection = locationSelection;
        String[] selectionArgs = {location};
        return weatherWithLocationQueryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

    }

    private Cursor getWeatherWithLocationAndDate(Uri uri, String[] projection, String sortOrder) {
        String location = WeatherContract.WeatherEntry.getLocationFromUri(uri);
        long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        String selection = locationSelection;
        String[] selectionArgs = {location, Long.toString(date)};
        return weatherWithLocationQueryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

    }

    @Override
    public boolean onCreate() {
        dbHelper = new WeatherDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case WEATHER:
                cursor = dbHelper.getReadableDatabase().query(WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case LOCATION:
                cursor = dbHelper.getReadableDatabase().query(WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case WEATHER_WITH_LOCATION:
                cursor = getWeatherWithLocation(uri, projection, sortOrder);
                break;

            case WEATHER_WITH_LOCATION_AND_DATE:
                cursor = getWeatherWithLocationAndDate(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

    }

    /*
    *   insert method is only useful for DIR based URIs (in this case WEATHER and LOCATION) because it will insert data in database tables
    *   We also need to notify listener for dataset change.
     */

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int matchCode = uriMatcher.match(uri);
        Uri returnUri = null;
        switch (matchCode) {
            case WEATHER: {
                long id = database.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = WeatherContract.WeatherEntry.weatherUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case LOCATION: {
                long id = database.insert(WeatherContract.LocationEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = WeatherContract.LocationEntry.locationUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }


        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int matchCode = uriMatcher.match(uri);
        int noOfRowsDeleted = 0;
        if (selection == null) selection = "1";           //this will delete all rows
        switch (matchCode) {
            case WEATHER:
                noOfRowsDeleted = database.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                noOfRowsDeleted = database.delete(WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (noOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return noOfRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int matchCode = uriMatcher.match(uri);
        int noOfRowsUpdated = 0;

        switch (matchCode) {
            case WEATHER:
                noOfRowsUpdated = database.update(WeatherContract.WeatherEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case LOCATION:
                noOfRowsUpdated = database.update(WeatherContract.LocationEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (noOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return noOfRowsUpdated;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:                                       //only weather because we have bulk of data in weather
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
