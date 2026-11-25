package com.example.icslauncher.settings;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick Settings panel: opens system settings for common toggles.
 * Replicates the ICS notification shade quick settings experience as a standalone activity.
 */
public class QuickSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static class Tile {
        final int index;
        final String settingsAction;
        Tile(int index, String settingsAction) {
            this.index = index;
            this.settingsAction = settingsAction;
        }
    }

    private final List<Tile> tiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_settings);

        // Define the quick settings tiles in order (left-to-right, top-to-bottom).
        tiles.add(new Tile(0, Settings.ACTION_WIFI_SETTINGS));
        tiles.add(new Tile(1, Settings.ACTION_BLUETOOTH_SETTINGS));
        tiles.add(new Tile(2, Settings.ACTION_DATA_USAGE_SETTINGS));
        tiles.add(new Tile(3, Settings.ACTION_SOUND_SETTINGS));
        tiles.add(new Tile(4, Settings.ACTION_DISPLAY_SETTINGS));
        tiles.add(new Tile(5, Settings.ACTION_BATTERY_SAVER_SETTINGS));
        tiles.add(new Tile(6, Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        tiles.add(new Tile(7, Settings.ACTION_SECURITY_SETTINGS));
        tiles.add(new Tile(8, Settings.ACTION_SYNC_SETTINGS));

        // Wire up each tile in the GridLayout.
        GridLayout grid = findViewById(R.id.qs_grid);
        if (grid != null) {
            for (int i = 0; i < grid.getChildCount(); i++) {
                View child = grid.getChildAt(i);
                final int idx = i;
                child.setOnClickListener(v -> {
                    for (Tile t : tiles) {
                        if (t.index == idx) {
                            Intent intent = new Intent(t.settingsAction);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                            break;
                        }
                    }
                });
            }
        }

        Button btnMore = findViewById(R.id.btn_more_settings);
        if (btnMore != null) {
            btnMore.setOnClickListener(v ->
                    startActivity(new Intent(Settings.ACTION_SETTINGS)));
        }
    }

    @Override
    public void onClick(View v) {
        // Not used with the new approach, kept for interface compliance.
    }
}
