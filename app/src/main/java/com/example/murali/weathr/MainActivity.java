package com.example.murali.weathr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        FragmentManager manager = getFragmentManager();
//        manager.beginTransaction().add(R.id.fragmentContainer,new ForecastListFragment())
//                .add(R.id.fragmentContainer,new TodayForecastFragment())
//                .commit();
    }

}
