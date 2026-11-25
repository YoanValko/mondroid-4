package com.example.icslauncher.launcher;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

/**
 * Represents a single launchable shortcut shown on the home-screen workspace or in the app drawer.
 */
public class AppModel {
    public CharSequence label;           // displayed name
    public String packageName;           // owning package (may be null for explicit components)
    public String className;             // activity class (may be null)
    public ComponentName component;      // explicit component to launch
    public Drawable icon;                // resolved icon
    public boolean systemApp;            // whether it came from the system image
    public IntentAction action;          // fallback action (e.g. dial)

    public AppModel(CharSequence label, ComponentName component) {
        this.label = label;
        this.component = component;
    }

    public AppModel(CharSequence label, String packageName, String className, Drawable icon) {
        this.label = label;
        this.packageName = packageName;
        this.className = className;
        this.icon = icon;
        if (packageName != null && className != null) {
            this.component = new ComponentName(packageName, className);
        }
    }

    public enum IntentAction {
        DIAL
    }
}
