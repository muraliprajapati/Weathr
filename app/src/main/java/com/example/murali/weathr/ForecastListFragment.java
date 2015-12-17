package com.example.murali.weathr;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.murali.weathr.database.WeatherContract;
import com.example.murali.weathr.database.WeatherDatabaseHelper;

/**
 * Created by Murali on 18-08-2015.
 */
public class ForecastListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    private static final int LIST_FRAGMENT_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.DATE,
            WeatherContract.WeatherEntry.SHORT_DESCRIPTION,
            WeatherContract.WeatherEntry.MAX_TEMP,
            WeatherContract.WeatherEntry.MIN_TEMP,
            WeatherContract.LocationEntry.LOCATION_CODE,
            WeatherContract.WeatherEntry.WEATHER_ID,
            WeatherContract.LocationEntry.COORD_LAT,
            WeatherContract.LocationEntry.COORD_LONG
    };
    static RecyclerView.Adapter mAdapter;
    static String[] weekForecast = {"Blah", "Blah", "Blah", "Blah", "Blah", "Blah", "Blah", "Blah"};
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    void setWeekForecast(String[] forecast) {
        weekForecast = forecast;
        mAdapter = new ForecastAdapter(weekForecast);
        //mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast_list, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Weathr");
        setHasOptionsMenu(true);
        WeatherDatabaseHelper database = new WeatherDatabaseHelper(getActivity());
        database.getWritableDatabase();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.forecastRecyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ForecastAdapter(weekForecast);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default_value));
        FetchWeatherTask task = new FetchWeatherTask(this);
        task.execute("Surat");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationString = WeatherUtility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.weatherWithLocationUri(locationString);
        return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
        String[] forecast;

        public ForecastAdapter(String[] forecast) {
            this.forecast = forecast;
        }

        @Override
        public ForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.single_forecast_row, parent, false);
            //ViewHolder viewHolder = new RecyclerView.ViewHolder(view);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ForecastAdapter.ViewHolder holder, int position) {
            holder.textView.setText(forecast[position]);
        }


        @Override
        public int getItemCount() {
            return forecast.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageView;
            TextView textView;
            RelativeLayout singleForecastRowLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                singleForecastRowLayout = (RelativeLayout) itemView.findViewById(R.id.singleForecastRowLayout);
                imageView = (ImageView) itemView.findViewById(R.id.singleForecastImageView);
                textView = (TextView) itemView.findViewById(R.id.singlePrimaryForecastTextView);
                textView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);

            }
        }
    }
}
