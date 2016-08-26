package com.example.murali.weathr;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Murali on 25/08/2016.
 */

public class WeatherCard extends Fragment {

    static Cursor cursor;

    ImageView forecastImageView;
    TextView locationTextView;
    TextView descriptionTextView;
    TextView dateTextView;
    TextView highTempTextView;
    TextView dayTextView;

    public static WeatherCard newInstance(int position, Cursor c) {
        cursor = c;
        Bundle args = new Bundle();
        args.putInt("position", position);

        WeatherCard fragment = new WeatherCard();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.new_single_row, container, false);
        int position = getArguments().getInt("position") + 1;
        forecastImageView = (ImageView) itemView.findViewById(R.id.forecast_image_view);
        descriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
        dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        highTempTextView = (TextView) itemView.findViewById(R.id.high_temp_text_view);
        dayTextView = (TextView) itemView.findViewById(R.id.day_text_view);
        setupData(position);
        return itemView;
    }

    private void setupData(int position) {
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(position);

            int weatherId = cursor.getInt(ForecastListFragment.COL_WEATHER_CONDITION_ID);
            //Log.i("tag", " " + weatherId);
            int defaultImage;

            defaultImage = WeatherUtility.getArtResourceForWeatherCondition(weatherId);
            boolean useLongToday = false;

            forecastImageView.setImageResource(defaultImage);

            long dateInMillis = cursor.getLong(ForecastListFragment.COL_WEATHER_DATE);
            String dateString = WeatherUtility.getFriendlyDayString(getActivity(), dateInMillis, useLongToday);
            dateTextView.setText(dateString);

            dayTextView.setText(WeatherUtility.getFormattedMonthDay(getActivity(), dateInMillis));

            String descriptionText = WeatherUtility.getStringForWeatherCondition(getActivity(), weatherId);
            //Log.i("tag", dateString);
            descriptionTextView.setText(descriptionText);

            double high = cursor.getDouble(ForecastListFragment.COL_WEATHER_MAX_TEMP);
            String highString = WeatherUtility.formatTemperature(getActivity(), high);
            highTempTextView.setText(highString);
        }
    }
}
