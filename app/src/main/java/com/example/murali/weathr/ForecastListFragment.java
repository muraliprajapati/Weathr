package com.example.murali.weathr;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.murali.weathr.database.WeatherContract;

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
    static final int COL_CITY_NAME = 7;
    static final int COL_COORD_LAT = 8;
    static final int COL_COORD_LONG = 9;


    private static final int LIST_FRAGMENT_LOADER = 1;

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

    String locationQuery;

    ForecastAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ViewPager pager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast_list, container, false);
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Weathr");
//        setHasOptionsMenu(true);
        locationQuery = WeatherUtility.getPreferredLocation(getActivity());


//        mRecyclerView = (RecyclerView) view.findViewById(R.id.forecastRecyclerView);
//        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        mRecyclerView.setLayoutManager(mLayoutManager);
        pager = (ViewPager) view.findViewById(R.id.forecastRecyclerView);
        pager.setClipToPadding(false);
        pager.setPadding(40, 0, 160, 40);
        pager.setPageMargin(40);
//        mAdapter = new ForecastAdapter(getActivity());

//        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LIST_FRAGMENT_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LIST_FRAGMENT_LOADER) {
            String locationString = WeatherUtility.getPreferredLocation(getActivity());
            String sortOrder = WeatherContract.WeatherEntry.DATE + " ASC";
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.weatherWithLocationUri(locationString);
            return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LIST_FRAGMENT_LOADER) {
//            mAdapter.swapCursor(cursor);
            pager.setAdapter(new ForecastPagerAdapter(getActivity().getSupportFragmentManager(), cursor));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapCursor(null);
    }


    void restart() {
        Log.i("tag", "loader restarted");
        getLoaderManager().restartLoader(LIST_FRAGMENT_LOADER, null, this);
    }

    class ForecastPagerAdapter extends FragmentStatePagerAdapter {
        Cursor cursor;

        public ForecastPagerAdapter(FragmentManager fm, Cursor cursor) {
            super(fm);
            this.cursor = cursor;
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return WeatherCard.newInstance(position, cursor);
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}


