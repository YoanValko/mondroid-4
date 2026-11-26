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
 * Tracks recently launched apps for the "Recent Apps" feature.
 * Uses SharedPreferences to persist launch timestamps.
 */
public class AppRecencyTracker {

    private static final String PREFIX = "app_launch_time_";
    private static final int MAX_RECENT_APPS = 10;

    private final SharedPreferences prefs;

    public AppRecencyTracker(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /** Record a launch of the given app package with current timestamp. */
    public void recordLaunch(String packageName) {
        if (packageName == null || packageName.isEmpty()) return;
        prefs.edit().putLong(PREFIX + packageName, System.currentTimeMillis()).apply();
    }

    /** Get the most recently launched apps. */
    public List<String> getRecentApps(int limit) {
        Map<String, Long> times = new HashMap<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            if (entry.getKey().startsWith(PREFIX)) {
                String pkg = entry.getKey().substring(PREFIX.length());
                long time = (Long) entry.getValue();
                // Only include apps launched within the last 24 hours
                if (now - time < 24 * 60 * 60 * 1000) {
                    times.put(pkg, time);
                }
            }
        }
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(times.entrySet());
        Collections.sort(sorted, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> a, Map.Entry<String, Long> b) {
            return Long.compare(b.getValue(), a.getValue());
            }
        });
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            result.add(sorted.get(i).getKey());
        }
        return result;
    }
}
