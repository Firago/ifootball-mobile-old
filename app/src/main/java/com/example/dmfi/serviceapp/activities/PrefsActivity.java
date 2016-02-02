package com.example.dmfi.serviceapp.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.example.dmfi.serviceapp.R;

/**
 * Created by dmfi on 12/10/2015.
 */
public class PrefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
