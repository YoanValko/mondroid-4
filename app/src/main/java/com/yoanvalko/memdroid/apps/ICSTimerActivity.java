package com.yoanvalko.memdroid.apps;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.util.Locale;

/**
 * Simple stopwatch using a handler for timing.
 */
public class ICSTimerActivity extends AppCompatActivity {

    private TextView timerView;
    private Button startButton;
    private Button resetButton;
    private Handler handler;
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Stopwatch");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        timerView = new TextView(this);
        timerView.setText("00:00.00");
        timerView.setTextSize(48);
        timerView.setPadding(0, 20, 0, 20);
        root.addView(timerView);

        startButton = new Button(this);
        startButton.setText("Start");
        startButton.setTextSize(18);
        startButton.setOnClickListener(v -> toggleTimer());
        root.addView(startButton);

        resetButton = new Button(this);
        resetButton.setText("Reset");
        resetButton.setTextSize(18);
        resetButton.setOnClickListener(v -> resetTimer());
        root.addView(resetButton);

        handler = new Handler();

        setContentView(root);
    }

    private void toggleTimer() {
        if (isRunning) {
            handler.removeCallbacks(updateTimer);
            isRunning = false;
            elapsedTime += System.currentTimeMillis() - startTime;
            startButton.setText("Start");
        } else {
            startTime = System.currentTimeMillis();
            handler.post(updateTimer);
            isRunning = true;
            startButton.setText("Stop");
        }
    }

    private void resetTimer() {
        handler.removeCallbacks(updateTimer);
        isRunning = false;
        elapsedTime = 0;
        startTime = 0;
        timerView.setText("00:00.00");
        startButton.setText("Start");
    }

    private final Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long elapsed = elapsedTime + (System.currentTimeMillis() - startTime);
            int seconds = (int) (elapsed / 1000);
            int millis = (int) (elapsed % 1000);
            timerView.setText(String.format(Locale.getDefault(), "%02d:%02d.%02d",
                    seconds / 60, seconds % 60, millis / 10));
            handler.postDelayed(this, 10);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimer);
    }
}
