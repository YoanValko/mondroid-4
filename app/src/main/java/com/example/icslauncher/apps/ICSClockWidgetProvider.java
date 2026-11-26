package com.example.icslauncher.apps;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.icslauncher.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ICS-style clock widget. Shows the current time and date, and opens the Clock app when tapped.
 */
public class ICSClockWidgetProvider extends AppWidgetProvider {

    private static final SimpleDateFormat TIME_FMT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Start updating when the first widget is added.
        Intent intent = new Intent(context, ICSClockWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, ICSClockWidgetProvider.class));
            for (int id : ids) {
                updateAppWidget(context, manager, id);
            }
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager manager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_clock);

        Date now = new Date();
        views.setTextViewText(R.id.widget_clock, TIME_FMT.format(now));
        views.setTextViewText(R.id.widget_date, DATE_FMT.format(now));

        // Tap to open Clock app.
        Intent clockIntent = new Intent(context, com.example.icslauncher.apps.ICSClockActivity.class);
        clockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent clockPending = PendingIntent.getActivity(context, 0, clockIntent,
                PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_clock, clockPending);

        manager.updateAppWidget(appWidgetId, views);
    }
}
