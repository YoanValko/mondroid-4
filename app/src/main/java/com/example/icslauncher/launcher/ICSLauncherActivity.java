package com.example.icslauncher.launcher;

import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.icslauncher.R;
import com.example.icslauncher.drawer.AppDrawerActivity;
import com.example.icslauncher.easteregg.EasterEggActivity;
import com.example.icslauncher.settings.AboutActivity;
import com.example.icslauncher.settings.QuickSettingsActivity;
import com.example.icslauncher.settings.SettingsActivity;
import com.example.icslauncher.wallpaper.WallpaperPickerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Ice Cream Sandwich-style home screen.
 * <p>
 * Registered as the HOME category activity so the user can select this package as the default
 * launcher (no root, no custom recovery, no unlocked bootloader required).
 * <p>
 * Supports multiple home screen pages via ViewPager2, page indicator dots, and long-press to
 * add app shortcuts or widgets.
 */
public class ICSLauncherActivity extends AppCompatActivity {

    private static final int PAGE_COUNT = 3;
    private static final int REQ_ADD_SHORTCUT = 2001;

    private static final class Shortcut {
        final int labelRes;
        final String packageName;
        final String className;
        Shortcut(int labelRes, String packageName, String className) {
            this.labelRes = labelRes;
            this.packageName = packageName;
            this.className = className;
        }
    }

    private static final Shortcut[] DEFAULT_SHORTCUTS = {
            new Shortcut(R.string.app_browser, "com.example.icslauncher", ".apps.ISBrowserActivity"),
            new Shortcut(R.string.app_email, "com.example.icslauncher", ".apps.ISEmailActivity"),
            new Shortcut(R.string.app_gallery, "com.example.icslauncher", ".apps.ICSGalleryActivity"),
            new Shortcut(R.string.app_music, "com.example.icslauncher", ".apps.ICSMusicActivity"),
            new Shortcut(R.string.app_camera, "com.example.icslauncher", ".apps.ICSCameraActivity"),
            new Shortcut(R.string.app_calendar, "com.example.icslauncher", ".apps.ICSCalendarActivity"),
            new Shortcut(R.string.app_contacts, "com.example.icslauncher", ".apps.ICSContactsActivity"),
            new Shortcut(R.string.app_clock, "com.example.icslauncher", ".apps.ICSClockActivity"),
            new Shortcut(R.string.app_calculator, "com.example.icslauncher", ".apps.ICSCalculatorActivity"),
            new Shortcut(R.string.app_messaging, "com.example.icslauncher", ".apps.ICSMessagingActivity"),
            new Shortcut(R.string.app_notes, "com.example.icslauncher", ".apps.ICSNotesActivity"),
            new Shortcut(R.string.app_file_manager, "com.example.icslauncher", ".apps.ICSFileManagerActivity"),
            new Shortcut(R.string.app_settings, "com.example.icslauncher", ".apps.ICSSettingsActivity"),
            new Shortcut(R.string.app_info, "com.example.icslauncher", ".apps.ICSAppInfoActivity"),
            new Shortcut(R.string.settings, "com.example.icslauncher", ".settings.SettingsActivity"),
            new Shortcut(R.string.wallpaper, "com.example.icslauncher", ".wallpaper.WallpaperPickerActivity"),
    };

    private SharedPreferences prefs;
    private int gridSpan;
    private ViewPager2 workspacePager;
    private WorkspacePagerAdapter pagerAdapter;
    private LinearLayout pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dark = p.getBoolean("dark_theme", false);
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        gridSpan = Integer.parseInt(prefs.getString("grid_size", String.valueOf(4)));

        // Search bar.
        SearchView searchBar = findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent drawer = new Intent(ICSLauncherActivity.this, AppDrawerActivity.class);
                drawer.putExtra("search_query", query);
                startActivity(drawer);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // ViewPager2 workspace.
        workspacePager = findViewById(R.id.workspace_pager);
        List<List<AppModel>> pages = buildDefaultPages();
        pagerAdapter = new WorkspacePagerAdapter(this, pages);
        workspacePager.setAdapter(pagerAdapter);
        workspacePager.setOffscreenPageLimit(1);

        // Page indicator.
        pageIndicator = findViewById(R.id.page_indicator);
        updatePageIndicator(0);
        workspacePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updatePageIndicator(position);
            }
        });

        // Long-press on workspace to add shortcuts/widgets.
        workspacePager.addOnLayoutChangeListener((v, left, top, right, bottom,
                                                  oldLeft, oldTop, oldRight, oldBottom) -> {
            v.setOnLongClickListener(v2 -> {
                showAddShortcutPopup(v2);
                return true;
            });
        });

        // Bottom-right floating shortcuts.
        ImageButton btnWallpaper = findViewById(R.id.btn_wallpaper);
        ImageButton btnQuickSettings = findViewById(R.id.btn_quick_settings);
        ImageButton btnSettings = findViewById(R.id.btn_settings);
        btnWallpaper.setOnClickListener(v ->
                startActivity(new Intent(ICSLauncherActivity.this, WallpaperPickerActivity.class)));
        btnQuickSettings.setOnClickListener(v ->
                startActivity(new Intent(ICSLauncherActivity.this, QuickSettingsActivity.class)));
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(ICSLauncherActivity.this, SettingsActivity.class)));

        // Bottom dock.
        buildDock();
        applyDockVisibility();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int s = Integer.parseInt(prefs.getString("grid_size", String.valueOf(4)));
        if (s != gridSpan) {
            gridSpan = s;
            // Rebuild pages with new span count.
            List<List<AppModel>> pages = buildDefaultPages();
            pagerAdapter = new WorkspacePagerAdapter(this, pages);
            workspacePager.setAdapter(pagerAdapter);
            updatePageIndicator(workspacePager.getCurrentItem());
        }
        applyDockVisibility();
    }

    private List<List<AppModel>> buildDefaultPages() {
        PackageManager pm = getPackageManager();
        List<List<AppModel>> pages = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) pages.add(new ArrayList<>());

        // Page 1: default shortcuts distributed across the grid.
        int span = gridSpan;
        List<AppModel> page1 = new ArrayList<>();
        for (Shortcut sc : DEFAULT_SHORTCUTS) {
            Drawable icon = null;
            try {
                icon = pm.getApplicationIcon(sc.packageName);
            } catch (PackageManager.NameNotFoundException ignored) {
                icon = getDrawable(R.drawable.ic_launcher);
            }
            page1.add(new AppModel(getString(sc.labelRes), sc.packageName, sc.className, icon));
        }
        pages.set(0, page1);

        return pages;
    }

    private void updatePageIndicator(int position) {
        pageIndicator.removeAllViews();
        int count = pagerAdapter.getPageCount();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(8, 8);
            lp.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(i == position ? R.drawable.page_indicator_dot_active
                    : R.drawable.page_indicator_dot_inactive);
            pageIndicator.addView(dot);
        }
    }

    private void buildDock() {
        LinearLayout dock = findViewById(R.id.dock);
        dock.removeAllViews();

        addDockIcon(R.drawable.ic_dock_back, R.string.shortcut_back, v -> onBackPressed());
        addDockIcon(R.drawable.ic_dock_phone, R.string.shortcut_phone, v -> {
            Intent dial = new Intent(Intent.ACTION_DIAL);
            startActivity(dial);
        });
        addDockIcon(R.drawable.ic_dock_browser, R.string.shortcut_browser, v ->
                startActivity(new Intent(this, com.example.icslauncher.apps.ISBrowserActivity.class)));
        addDockIcon(R.drawable.ic_dock_email, R.string.shortcut_email, v ->
                startActivity(new Intent(this, com.example.icslauncher.apps.ISEmailActivity.class)));
        addDockIcon(R.drawable.ic_dock_camera, R.string.shortcut_camera, v ->
                startActivity(new Intent(this, com.example.icslauncher.apps.ICSCameraActivity.class)));
        addDockIcon(R.drawable.ic_dock_recent, R.string.shortcut_recent_apps, v -> openAppDrawer());
        addDockIcon(R.drawable.ic_action_apps, R.string.shortcut_all_apps, v -> openAppDrawer());
    }

    private void addDockIcon(int iconRes, int labelRes, View.OnClickListener listener) {
        LinearLayout dock = findViewById(R.id.dock);
        LinearLayout slot = new LinearLayout(this);
        slot.setOrientation(LinearLayout.VERTICAL);
        slot.setGravity(Gravity.CENTER);
        slot.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        slot.setPadding(8, 4, 8, 4);

        ImageButton icon = new ImageButton(this);
        icon.setImageResource(iconRes);
        icon.setBackground(null);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(48, 48);
        icon.setLayoutParams(lp);
        icon.setContentDescription(getString(labelRes));
        icon.setOnClickListener(listener);

        TextView label = new TextView(this);
        label.setText(getString(labelRes));
        label.setTextSize(12);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(2);
        label.setEllipsize(android.text.TextUtils.TruncateAt.END);
        label.setTextColor(getResources().getColor(R.color.colorOnSurface, null));
        label.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        slot.addView(icon);
        slot.addView(label);
        slot.setOnClickListener(listener);
        dock.addView(slot);
    }

    private void applyDockVisibility() {
        boolean show = prefs.getBoolean("show_dock", true);
        findViewById(R.id.dock_container).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void openAppDrawer() {
        startActivity(new Intent(this, AppDrawerActivity.class));
    }

    /** Show a popup to add an app shortcut or widget to the current page. */
    private void showAddShortcutPopup(View anchor) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_add_shortcut, null);
        PopupWindow popup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setElevation(8f);
        popup.showAtLocation(anchor, Gravity.CENTER, 0, 0);

        Button btnAddApp = popupView.findViewById(R.id.btn_add_app);
        Button btnAddWidget = popupView.findViewById(R.id.btn_add_widget);
        Button btnCancel = popupView.findViewById(R.id.btn_cancel);

        btnAddApp.setOnClickListener(v -> {
            popup.dismiss();
            openAppDrawer();
        });

        btnAddWidget.setOnClickListener(v -> {
            popup.dismiss();
            openWidgetPicker();
        });

        btnCancel.setOnClickListener(v -> popup.dismiss());

        // Dismiss on outside touch.
        popup.setTouchInterceptor((v, event) -> false);
    }

    private void openWidgetPicker() {
        Intent pick = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        startActivityForResult(pick, REQ_ADD_SHORTCUT);
    }

    /** Launch the selected shortcut. Falls back gracefully if the component cannot resolve. */
    private void launchApp(AppModel app) {
        Intent intent;
        if (app.component != null) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(app.component);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (app.packageName != null) {
            intent = getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (intent == null) {
                showToast("Cannot open " + app.label);
                return;
            }
        } else {
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showToast("Cannot open " + app.label);
        }
    }

    private void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /** Resolve every installed launcher app (used by the app drawer). */
    static public List<AppModel> queryLauncherApps(PackageManager pm) {
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(main, 0);
        List<AppModel> list = new ArrayList<>(resolveInfos.size());
        for (ResolveInfo ri : resolveInfos) {
            if (ri.activityInfo == null) continue;
            AppModel m = new AppModel(ri.loadLabel(pm),
                    ri.activityInfo.packageName, ri.activityInfo.name,
                    ri.activityInfo.loadIcon(pm));
            m.systemApp = (ri.activityInfo.applicationInfo.flags &
                    android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
            list.add(m);
        }
        list.sort((a, b) -> {
            String la = a.label != null ? a.label.toString() : "";
            String lb = b.label != null ? b.label.toString() : "";
            return la.compareToIgnoreCase(lb);
        });
        return list;
    }
}
