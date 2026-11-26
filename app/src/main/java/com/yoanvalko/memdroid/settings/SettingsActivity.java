package com.yoanvalko.memdroid.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.yoanvalko.memdroid.R;

/**
 * Launcher settings: grid size, dark theme, dock visibility, and the Easter egg.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, new ICSPreferenceFragment())
                    .commit();
        }
    }
}
