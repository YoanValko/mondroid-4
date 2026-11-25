package com.example.icslauncher.apps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

/**
 * Reimplementation of a Nexus-era Settings app. Opens system settings pages
 * for common configuration options.
 */
public class ICSSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Settings");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        addSettingRow(root, "Wireless & networks", Settings.ACTION_WIRELESS_SETTINGS);
        addSettingRow(root, "Display", Settings.ACTION_DISPLAY_SETTINGS);
        addSettingRow(root, "Sound", Settings.ACTION_SOUND_SETTINGS);
        addSettingRow(root, "Location & security", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        addSettingRow(root, "Applications", Settings.ACTION_APPLICATION_SETTINGS);
        addSettingRow(root, "Accounts & sync", Settings.ACTION_SYNC_SETTINGS);
        addSettingRow(root, "Privacy", Settings.ACTION_PRIVACY_SETTINGS);
        addSettingRow(root, "Storage", Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        addSettingRow(root, "About phone", Settings.ACTION_DEVICE_INFO_SETTINGS);

        setContentView(root);
    }

    private void addSettingRow(LinearLayout root, String title, String action) {
        Button btn = new Button(this);
        btn.setText(title);
        btn.setTextSize(16);
        btn.setGravity(android.view.Gravity.LEFT | android.view.Gravity.CENTER_VERTICAL);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btn.setPadding(16, 12, 16, 12);
        btn.setTag(action);
        btn.setOnClickListener(this);
        root.addView(btn);
    }

    @Override
    public void onClick(View v) {
        String action = (String) v.getTag();
        Intent intent = new Intent(action);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
