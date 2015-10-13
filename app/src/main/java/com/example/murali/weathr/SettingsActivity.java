package com.example.murali.weathr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Murali on 22-08-2015.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();

    }
}
