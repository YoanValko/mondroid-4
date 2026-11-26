package com.yoanvalko.memdroid.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks app launch frequency for the "Frequent Apps" feature.
 * Uses SharedPreferences to persist launch counts.
 */
public class AppFrequencyTracker {

    private static final String PREFIX = "app_launch_count_";
    private static final int MAX_FREQUENT_APPS = 12;

    private final SharedPreferences prefs;

    public AppFrequencyTracker(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /** Record a launch of the given app package. */
    public void recordLaunch(String packageName) {
        if (packageName == null || packageName.isEmpty()) return;
        int count = prefs.getInt(PREFIX + packageName, 0);
        prefs.edit().putInt(PREFIX + packageName, count + 1).apply();
    }

    /** Get the top N most frequently launched apps. */
    public List<String> getTopApps(int limit) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            if (entry.getKey().startsWith(PREFIX)) {
                String pkg = entry.getKey().substring(PREFIX.length());
                counts.put(pkg, (Integer) entry.getValue());
            }
        }
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            result.add(sorted.get(i).getKey());
        }
        return result;
    }

    public int getLaunchCount(String packageName) {
        return prefs.getInt(PREFIX + packageName, 0);
    }
}
