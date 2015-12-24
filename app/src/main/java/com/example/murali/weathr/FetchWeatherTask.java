package com.example.murali.weathr;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.murali.weathr.database.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

/**
 * Created by Murali on 08-07-2015.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    public static final String TAG = "FetchWeatherTask";
    public static final String API_URL_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    //"http://api.openweathermap.org/data/2.5/forecast/daily?q=395010&units=metric&cnt=7
    public static final String API_QUESTION = "?";
    public static final String API_Q = "q";
    public static final String API_AMPERSAND = "&";
    public static final String API_UNIT = "metric";
    public static final String API_DAY_COUNT = "7";
    public static final String APP_ID = "789c7a808690dc32dbf1324ad4b2e1e3";

    Context context;
    //    String locationQuery = WeatherUtility.getPreferredLocation(context);
    String locationQuery = "395010";

    FetchWeatherTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... location) {
        HttpURLConnection connection = null;
        try {
            String url = Uri.parse(API_URL_BASE).buildUpon()
                    .appendQueryParameter("q", location[0])
                    .appendQueryParameter("units", API_UNIT)
                    .appendQueryParameter("cnt", API_DAY_COUNT)
                    .appendQueryParameter("appid", APP_ID)
                    .build().toString();
            Log.i(TAG, url);
            URL apiUrl = new URL(url);

            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String jsonString = readJSON(inputStream);

            Log.i(TAG, jsonString);

            parseJsonData(jsonString, locationQuery);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);


    }

    private String readJSON(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    private void parseJsonData(String jsonString, String locationSetting) throws JSONException {
        Calendar calendar = new GregorianCalendar();
        long todayInMillis = calendar.getTimeInMillis();
        calendar.clear();
        int pastDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_WEEK, pastDay);
        long pastDayInMillis = calendar.getTimeInMillis();


        JSONObject cityWeather = new JSONObject(jsonString);
        JSONArray weekForecaast = cityWeather.getJSONArray("list");

        JSONObject cityJson = cityWeather.getJSONObject("city");
        String cityName = cityJson.getString("name");

        JSONObject cityCoord = cityJson.getJSONObject("coord");
        double cityLatitude = cityCoord.getDouble("lat");
        double cityLongitude = cityCoord.getDouble("lon");

        long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(weekForecaast.length());

        String sortOrder = WeatherContract.WeatherEntry.DATE + " ASC";
        String locationString = WeatherUtility.getPreferredLocation(getContext());
        Uri uri = WeatherContract.WeatherEntry.CONTENT_URI;
        String selection = WeatherContract.WeatherEntry.DATE + " >= ?";
        String[] selectionArgs = new String[]{Long.toString(todayInMillis)};
        String[] projection = new String[]{WeatherContract.WeatherEntry.DATE};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor.moveToFirst() && cursor.getCount() <= 7) {
            Log.i("tag", "FetchWeatehrTask Cursor " + cursor.getCount() + " rows");
            getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                    WeatherContract.WeatherEntry.DATE + " < ?",
                    new String[]{Long.toString(pastDayInMillis)});
            cursor.close();

        } else {
            for (int i = 0; i < 7; i++) {
                cityWeather = new JSONObject(jsonString);
                weekForecaast = cityWeather.getJSONArray("list");
                JSONObject dayForecast = weekForecaast.getJSONObject(i);
                JSONObject mainForecast = dayForecast.getJSONObject("temp");
                Double minTemp = mainForecast.getDouble("min");
                Double maxTemp = mainForecast.getDouble("max");
                Double pressure = dayForecast.getDouble("pressure");
                int humidity = dayForecast.getInt("humidity");
                Double windSpeed = dayForecast.getDouble("speed");
                Double windDirection = dayForecast.getDouble("deg");
                long dayTime = getDayTimeInMillis(i);

                JSONObject weatherObject = dayForecast.getJSONArray("weather").getJSONObject(0);
                String description = weatherObject.getString("main");
                int weatherId = weatherObject.getInt("id");


                ContentValues weatherValues = new ContentValues();

                weatherValues.put(WeatherContract.WeatherEntry.LOCATION_ID, locationId);
                weatherValues.put(WeatherContract.WeatherEntry.DATE, dayTime);
                weatherValues.put(WeatherContract.WeatherEntry.HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.MAX_TEMP, maxTemp);
                weatherValues.put(WeatherContract.WeatherEntry.MIN_TEMP, minTemp);
                weatherValues.put(WeatherContract.WeatherEntry.SHORT_DESCRIPTION, description);
                weatherValues.put(WeatherContract.WeatherEntry.WEATHER_ID, weatherId);

                getContext().getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);
                getContext().getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
                        WeatherContract.WeatherEntry.DATE + " < ?",
                        new String[]{Long.toString(todayInMillis)});


            }
        }

    }

    public long getDayTimeInMillis(int i) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, i);
        return calendar.getTimeInMillis();
    }

    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        // First, check if the location with this city name exists in the db
        Cursor locationCursor = getContext().getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.LOCATION_CODE + " = ?",
                new String[]{cityName},
                null);


        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {

            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            locationValues.put(WeatherContract.LocationEntry.LOCATION_CODE, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.CITY_NAME, cityName);

            locationValues.put(WeatherContract.LocationEntry.COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COORD_LONG, lon);

            // Finally, insert location data into the database.
            Uri insertedUri = getContext().getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);

        }
        locationCursor.close();
        return locationId;
    }

    public Context getContext() {
        return context;
    }

}
