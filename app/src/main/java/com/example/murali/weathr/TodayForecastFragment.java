package com.example.murali.weathr;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.murali.weathr.database.WeatherContract;


/**
 * Created by Murali on 22/12/2015.
 */

public class TodayForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int TODAY_FRAGMENT_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {

            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.DATE,
            WeatherContract.WeatherEntry.SHORT_DESCRIPTION,
            WeatherContract.WeatherEntry.MAX_TEMP,
            WeatherContract.WeatherEntry.MIN_TEMP,
            WeatherContract.LocationEntry.LOCATION_CODE,
            WeatherContract.WeatherEntry.WEATHER_ID,
            WeatherContract.LocationEntry.CITY_NAME,
            WeatherContract.LocationEntry.COORD_LAT,
            WeatherContract.LocationEntry.COORD_LONG
    };
    static ForecastAdapter adapter;
    Cursor cursor;
    boolean useLongToday;
    ImageView forecastImageView;
    TextView locationTextView;
    TextView descriptionTextView;
    TextView dateTextView;
    TextView highTempTextView;
    TextView lowTempTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.today_forecast_layout, container, false);
        Log.i("tag", "inside onCreateView");
        adapter = new ForecastAdapter(getActivity());
        forecastImageView = (ImageView) itemView.findViewById(R.id.forecast_image_view);
        locationTextView = (TextView) itemView.findViewById(R.id.location_text_view);
        descriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
        dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        highTempTextView = (TextView) itemView.findViewById(R.id.high_temp_text_view);
        lowTempTextView = (TextView) itemView.findViewById(R.id.low_temp_text_view);

        return itemView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TODAY_FRAGMENT_LOADER, null, this);
        adapter = new ForecastAdapter(getActivity());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.i("tag", "inside onCreateLoader");
        if (i == TODAY_FRAGMENT_LOADER) {
            String locationString = WeatherUtility.getPreferredLocation(getActivity());
            String sortOrder = WeatherContract.WeatherEntry.DATE + " ASC";
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.weatherWithLocationUri(locationString);
            return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == TODAY_FRAGMENT_LOADER) {
            Log.i("tag", "inside onLoadFinished");
            if (cursor != null) {
                this.cursor = cursor;
                setupData(cursor);
                Log.i("tag", "I Got the CURSOR with " + cursor.getCount() + " rows");
            } else Toast.makeText(getActivity(), "Bad Luck Again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    void setupData(Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int weatherId = cursor.getInt(ForecastListFragment.COL_WEATHER_CONDITION_ID);
            // Log.i("tag", " " + weatherId);
            int defaultImage;


            defaultImage = WeatherUtility.getArtResourceForWeatherCondition(weatherId);
            useLongToday = true;


            forecastImageView.setImageResource(defaultImage);

            long dateInMillis = cursor.getLong(ForecastListFragment.COL_WEATHER_DATE);
            String dateString = WeatherUtility.getFriendlyDayString(getActivity(), dateInMillis, useLongToday);
            dateTextView.setText(dateString);

            String descriptionText = WeatherUtility.getStringForWeatherCondition(getActivity(), weatherId);
            Log.i("tag", dateString);
            descriptionTextView.setText(descriptionText);

            double high = cursor.getDouble(ForecastListFragment.COL_WEATHER_MAX_TEMP);
            String highString = WeatherUtility.formatTemperature(getActivity(), high);
            highTempTextView.setText(highString);

            double low = cursor.getDouble(ForecastListFragment.COL_WEATHER_MIN_TEMP);
            String lowString = WeatherUtility.formatTemperature(getActivity(), low);
            lowTempTextView.setText(lowString);

            String cityName = cursor.getString(ForecastListFragment.COL_CITY_NAME);
            //Log.i("tag", cityName);
            locationTextView.setText(cityName);
        } else {
            Toast.makeText(getActivity(), "Bad Luck!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        getLoaderManager().restartLoader(TODAY_FRAGMENT_LOADER, null, this);
    }

    void restart() {
        Log.i("tag", "loader restarted");
        getLoaderManager().restartLoader(TODAY_FRAGMENT_LOADER, null, this);
    }
}
