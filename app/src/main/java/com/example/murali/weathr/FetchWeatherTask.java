package com.example.murali.weathr;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Murali on 08-07-2015.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
    public static final String TAG = "FetchWeatherTask";
    public static final String API_URL_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    //"http://api.openweathermap.org/data/2.5/forecast/daily?q=395010&units=metric&cnt=7
    public static final String API_QUESTION = "?";
    public static final String API_Q = "q";
    public static final String API_AMPERSAND = "&";
    public static final String API_UNIT = "metric";
    public static final String API_DAY_COUNT = "7";
    public static final String APP_ID = "789c7a808690dc32dbf1324ad4b2e1e3";

    SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd-MMM-yyyy");
    String[] forecastData;
    ForecastListFragment fragment;

    FetchWeatherTask(ForecastListFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected String[] doInBackground(String... location) {
        try {
            String url = Uri.parse(API_URL_BASE).buildUpon()
                    .appendQueryParameter("q", location[0])
                    .appendQueryParameter("units", API_UNIT)
                    .appendQueryParameter("cnt", API_DAY_COUNT)
                    .appendQueryParameter("appid", APP_ID)
                    .build().toString();
            Log.i(TAG, url);
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            String jsonString = readJSON(inputStream);
            Log.i(TAG, jsonString);
            forecastData = parseJsonData(jsonString);
            return forecastData;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);

        fragment.setWeekForecast(result);


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

    private String[] parseJsonData(String jsonString) throws JSONException {
        String[] weeklyForecast = new String[7];


        for (int i = 0; i < 7; i++) {
            JSONObject cityWeather = new JSONObject(jsonString);
            JSONArray weekForecaast = cityWeather.getJSONArray("list");
            JSONObject dayForecast = weekForecaast.getJSONObject(i);
            JSONObject mainForecast = dayForecast.getJSONObject("temp");
            Double minTemp = mainForecast.getDouble("min");
            Double maxTemp = mainForecast.getDouble("max");
            long dayTime = getDay(i);
            String forecastData = "Min : " + minTemp + " " + "Max : " + maxTemp;
            weeklyForecast[i] = forecastData;

            //Log.i(TAG, "" + minTemp);
            //Log.i(TAG, "" + maxTemp);

        }
        return weeklyForecast;
    }

    public long getDay(int i) {
        Calendar calendar = new GregorianCalendar();

        calendar.add(Calendar.DAY_OF_MONTH, i);
        return calendar.getTimeInMillis();


    }
}
