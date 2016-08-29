package com.example.murali.weathr;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.murali.weathr.database.WeatherContract;

/**
 * Created by Murali on 29/08/2016.
 */

public class ForecastNotification {
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_CITY_NAME = 7;
    private static final String TAG = "ForecastNotification";
    private static final String[] FORECAST_COLUMNS = {

            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.DATE,
            WeatherContract.WeatherEntry.SHORT_DESCRIPTION,
            WeatherContract.WeatherEntry.MAX_TEMP,
            WeatherContract.WeatherEntry.MIN_TEMP,
            WeatherContract.LocationEntry.LOCATION_CODE,
            WeatherContract.WeatherEntry.WEATHER_ID,
            WeatherContract.LocationEntry.CITY_NAME,
    };
    Context context;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager manager;
    Cursor cursor;
    String description;
    double temp;
    int weatherId, imageId;

    ForecastNotification(Context context) {
        this.context = context;
        String sortOrder = WeatherContract.WeatherEntry.DATE + " ASC";
        cursor = context.getContentResolver()
                .query(WeatherContract.WeatherEntry.weatherWithLocationUri(WeatherUtility.getPreferredLocation(context)),
                        FORECAST_COLUMNS, null, null, sortOrder);
        cursor.moveToFirst();
        description = cursor.getString(COL_WEATHER_DESC);
        temp = cursor.getDouble(COL_WEATHER_MAX_TEMP);
        weatherId = cursor.getInt(COL_WEATHER_CONDITION_ID);
        imageId = WeatherUtility.getArtResourceForWeatherCondition(weatherId);

    }

    private void setup() {

        String notificationString = WeatherUtility.getNotificationString(context, temp, description);
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(imageId)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(notificationString)
                .setOngoing(true);
    }

    public void show() {
        setup();

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notificationBuilder.build());
        Log.i(TAG, "show: ");
    }

    public void hide() {
        if (manager != null) {
            manager.cancel(1);
        }
    }


}
