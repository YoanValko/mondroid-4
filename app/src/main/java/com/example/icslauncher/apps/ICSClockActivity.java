package com.example.icslauncher.apps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Reimplementation of the ICS Clock. Shows the current time/date and offers a basic (non-persistent)
 * alarm UI. A true alarm provider requires the system Clock app; this is a sandbox-safe placeholder.
 */
public class ICSClockActivity extends AppCompatActivity {

    private TextView timeView;
    private TextView dateView;
    private final Handler handler = new Handler();
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
    private final Runnable clockTick = new Runnable() {
        @Override
        public void run() {
            Date now = new Date();
            timeView.setText(timeFmt.format(now));
            dateView.setText(dateFmt.format(now));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(24, 24, 24, 24);

        timeView = new TextView(this);
        timeView.setTextSize(64);
        timeView.setText("00:00");
        root.addView(timeView);

        dateView = new TextView(this);
        dateView.setTextSize(20);
        dateView.setPadding(0, 8, 0, 24);
        root.addView(dateView);

        Button btnAlarm = new Button(this);
        btnAlarm.setText("Set alarm (opens system clock)");
        btnAlarm.setOnClickListener(v -> openSystemClock());
        root.addView(btnAlarm);

        Button btnVibrate = new Button(this);
        btnVibrate.setText("Vibrate (haptic demo)");
        btnVibrate.setOnClickListener(v -> {
            android.os.Vibrator vib = (android.os.Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib != null && vib.hasVibrator()) vib.vibrate(60);
        });
        root.addView(btnVibrate);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(clockTick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(clockTick);
    }

    private void openSystemClock() {
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "System clock not available", Toast.LENGTH_SHORT).show();
        }
    }
}
