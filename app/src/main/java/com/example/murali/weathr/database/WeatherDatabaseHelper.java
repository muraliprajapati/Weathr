package com.example.murali.weathr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.murali.weathr.database.WeatherContract.LocationEntry;
import com.example.murali.weathr.database.WeatherContract.WeatherEntry;

/**
 * Created by Murali on 08/10/2015.
 */
public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "weather.db";
    public static final String location_table = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + " INTEGER PRIMARY KEY, " + LocationEntry.LOCATION_CODE + " TEXT NOT NULL, " + LocationEntry.COORD_LAT + " REAL NOT NULL, " + LocationEntry.COORD_LONG + " REAL NOT NULL);";
    public static final String weather_table = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" + WeatherEntry._ID + " INTEGER NOT NULL, " + WeatherEntry.LOCATION_ID + " INTEGER NOT NULL, " + WeatherEntry.DATE + " INTEGER NOT NULL, " + WeatherEntry.SHORT_DESCRIPTION + " TEXT NOT NULL, " + WeatherEntry.WEATHER_ID + " INTEGER NOT NULL, " + WeatherEntry.MIN_TEMP + " REAL NOT NULL, " + WeatherEntry.MAX_TEMP + " REAL NOT NULL, " + WeatherEntry.HUMIDITY + " REAL NOT NULL, " + WeatherEntry.PRESSURE + " REAL NOT NULL, " + WeatherEntry.WIND_SPEED + " REAL NOT NULL, " + WeatherEntry.DEGREES + " REAL NOT NULL, FOREIGN KEY (" + WeatherEntry.LOCATION_ID + ") REFERENCES " + LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), UNIQUE (" + WeatherEntry.DATE + ", " + WeatherEntry.LOCATION_ID + "));";
    public static final String drop_table = "DROP TABLE IF EXISTS ";
    private static final int DATABASE_VERSION = 1;
    Context context;
    /*
    *SQLite statements for creating Weather and Location Table
    * CREATE TABLE WEATHER (ID INTEGER NOT NULL, LOCATION_ID INTEGER NOT NULL, DATE INTEGER NOT NULL, SHORT_DESCRIPTION TEXT NOT NULL, WEATHER_ID INTEGER NOT NULL, MIN_TEMP REAL NOT NULL, MAX_TEMP REAL NOT NULL, HUMIDDITY REAL NOT NULL, PRESSURE REAL NOT NULL, WIND_SPEED REAL NOT NULL, DEGREES REAL NOT NULL, FOREIGN KEY ( LOCATION_ID ) REFERENCES LOCATION_TABLE ( ID ), UNIQUE ( DATE, LOCATION_ID ) NO CONFLICT REPLACE;
    * CREATE TABLE LOCATION (ID INTEGER PRIMARY KEY, LOCATION_CODE TEXT NOT NULL, COORD_LAT REAL NOT NULL, COORDD_LONG REAL NOT NULL);
     */
    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(location_table);
        sqLiteDatabase.execSQL(weather_table);
        Toast.makeText(context, "Tables Created Successfully", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(drop_table + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(drop_table + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
