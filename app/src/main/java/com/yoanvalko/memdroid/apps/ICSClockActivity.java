package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ICSClockActivity extends AppCompatActivity {

    private android.widget.TextView timeView;
    private TextView dateView;
    private Handler handler;
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

        timeView = new android.widget.TextView(this);
        timeView.setTextSize(64);
        timeView.setText("00:00");
        root.addView(timeView);

        dateView = new TextView(this);
        dateView.setTextSize(20);
        dateView.setPadding(0, 8, 0, 24);
        root.addView(dateView);

        Button btnAlarm = new Button(this);
        btnAlarm.setText("Open system clock settings");
        btnAlarm.setOnClickListener(v -> {
            android.content.Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "System clock not available", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(btnAlarm);

        Button btnVibrate = new Button(this);
        btnVibrate.setText("Vibrate (haptic demo)");
        btnVibrate.setOnClickListener(v -> {
            Vibrator vib = (Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);
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
}
