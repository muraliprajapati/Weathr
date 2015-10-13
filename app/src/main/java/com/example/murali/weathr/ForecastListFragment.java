package com.example.murali.weathr;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.example.murali.weathr.database.WeatherDatabase;

import java.util.WeakHashMap;

/**
 * Created by Murali on 18-08-2015.
 */
public class ForecastListFragment extends Fragment {
    RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    static String[] weekForecast = {"Blah", "Blah", "Blah", "Blah", "Blah", "Blah", "Blah", "Blah"};

    void setWeekForecast(String[] forecast) {
        weekForecast = forecast;
        mAdapter = new ForecastAdapter(forecast);
        mAdapter.notifyDataSetChanged();
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
        WeatherDatabase database = new WeatherDatabase(getActivity());
        database.getWritableDatabase();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.forecastRecyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ForecastAdapter(weekForecast);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
        String[] forecast;

        public ForecastAdapter(String[] forecast) {
            this.forecast = forecast;
        }

        @Override
        public ForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.single_forecast_row, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ForecastAdapter.ViewHolder holder, int position) {
            holder.textView.setText(forecast[position]);
        }

        @Override
        public int getItemCount() {
            return forecast.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.singleForecastImageView);
                textView = (TextView) itemView.findViewById(R.id.singlePrimaryForecastTextView);

            }
        }
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
        task.execute(location);
    }
}
