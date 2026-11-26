package com.yoanvalko.memdroid.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;
import com.yoanvalko.memdroid.launcher.ICSLauncherActivity;

/**
 * First-run welcome screen. Shown once; after that the launcher opens directly.
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_WELCOME_SHOWN = "welcome_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(PREFS_WELCOME_SHOWN, false)) {
            // Already shown — go straight to the launcher.
            startActivity(new Intent(this, ICSLauncherActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        Button btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(v -> {
            prefs.edit().putBoolean(PREFS_WELCOME_SHOWN, true).apply();
            startActivity(new Intent(WelcomeActivity.this, ICSLauncherActivity.class));
            finish();
        });
    }
}
