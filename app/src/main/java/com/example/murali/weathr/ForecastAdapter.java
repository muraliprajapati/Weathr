package com.example.murali.weathr;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Murali on 18/12/2015.
 */
class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    Context context;
    Cursor cursor;
    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.today_forecast_layout;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.single_forecast_row;
                    break;
                }
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int weatherId = cursor.getInt(ForecastListFragment.COL_WEATHER_ID);
        int defaultImage;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                defaultImage = WeatherUtility.getArtResourceForWeatherCondition(weatherId);
                //useLongToday = true;
                break;
            default:
                defaultImage = WeatherUtility.getIconResourceForWeatherCondition(weatherId);
                //useLongToday = false;
        }
//
//        if ( WeatherUtility.usingLocalGraphics(context) ) {
//            holder.forecastImageView.setImageResource(defaultImage);
//        }

    }


    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView forecastImageView;
        TextView locationTextView;
        TextView descriptionTextView;
        TextView dateTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            forecastImageView = (ImageView) itemView.findViewById(R.id.forecast_image_view);
            locationTextView = (TextView) itemView.findViewById(R.id.location_text_view);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
        }

        @Override
        public void onClick(View view) {


        }
    }
}
