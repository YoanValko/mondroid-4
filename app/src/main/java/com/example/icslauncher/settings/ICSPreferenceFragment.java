package com.example.icslauncher.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.icslauncher.R;
import com.example.icslauncher.easteregg.EasterEggActivity;

/**
 * Preference UI for the ICS Launcher. Changes to grid size / dock are read by the launcher;
 * changing the dark theme recreates the activity so the theme applies immediately.
 */
public class ICSPreferenceFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference easterEgg = findPreference("easter_egg");
        if (easterEgg != null) {
            easterEgg.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireContext(), EasterEggActivity.class));
                return true;
            });
        }

        Preference version = findPreference("version");
        if (version != null) {
            version.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.settings_version)
                        .setMessage(getString(R.string.settings_version_value) + "\n\nIce Cream Sandwich experience reimplemented for Android 10–16.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            });
        }

        Preference about = findPreference("about");
        if (about != null) {
            about.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(requireContext(), AboutActivity.class));
                return true;
            });
        }

        // Summary helpers.
        ListPreference grid = findPreference("grid_size");
        if (grid != null) {
            grid.setOnPreferenceChangeListener((preference, newValue) -> {
                preference.setSummary(newValue + " × " + newValue);
                return true;
            });
            grid.setSummary(grid.getValue() + " × " + grid.getValue());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {
        if ("dark_theme".equals(key)) {
            // Recreate so the new theme/window background applies immediately.
            requireActivity().recreate();
        }
        // grid_size and show_dock are read by the launcher itself (see ICSLauncherActivity),
        // so nothing else is required here.
    }
}
