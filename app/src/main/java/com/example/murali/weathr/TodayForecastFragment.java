package com.example.murali.weathr;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Murali on 22/12/2015.
 */
public class TodayForecastFragment extends Fragment {

    Cursor cursor;
    boolean useLongToday;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.today_forecast_layout, container, false);
        ImageView forecastImageView;
        TextView locationTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        TextView highTempTextView;
        TextView lowTempTextView;

        forecastImageView = (ImageView) itemView.findViewById(R.id.forecast_image_view);
        locationTextView = (TextView) itemView.findViewById(R.id.location_text_view);
        descriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
        dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        highTempTextView = (TextView) itemView.findViewById(R.id.high_temp_text_view);
        lowTempTextView = (TextView) itemView.findViewById(R.id.low_temp_text_view);

        int weatherId = cursor.getInt(ForecastListFragment.COL_WEATHER_CONDITION_ID);
        Log.i("tag", " " + weatherId);
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
        Log.i("tag", cityName);
        //holder.locationTextView.setText(cityName);

        return itemView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ForecastAdapter adapter = new ForecastAdapter(getActivity());
        cursor = adapter.getCursor();
        cursor.moveToPosition(0);
    }
}
