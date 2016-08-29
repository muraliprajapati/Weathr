package com.example.murali.weathr;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Murali on 22-08-2015.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";
    ForecastNotification notification;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);
        notification = new ForecastNotification(getActivity());
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_notification_key)));


    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.


        if (preference.getKey().equals(getString(R.string.pref_location_key))) {
            onPreferenceChange(preference, PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), ""));
        } else if (preference.getKey().equals(getString(R.string.pref_unit_key))) {
            onPreferenceChange(preference, PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), getString(R.string.pref_unit_default_value)));
        } else if (preference.getKey().equals(getString(R.string.pref_show_notification_key))) {
            onPreferenceChange(preference, PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getBoolean(preference.getKey(), true));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(newValue.toString());
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
            Log.i(TAG, "onPreferenceChange: ");
            notification.show();
        } else if (preference instanceof CheckBoxPreference) {
            preference.setSummary("");
            Boolean b = (Boolean) newValue;
            if (b) notification.show();
            else notification.hide();
        } else {
            preference.setSummary(newValue.toString());
        }
        return true;
    }
}
