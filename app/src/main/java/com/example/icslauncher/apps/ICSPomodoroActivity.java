package com.example.icslauncher.apps;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.Locale;

/**
 * Pomodoro Timer - 25 minutes work, 5 minutes break.
 */
public class ICSPomodoroActivity extends AppCompatActivity {

    private TextView timerView;
    private TextView statusView;
    private Button startButton;
    private Button resetButton;
    private Handler handler;

    private enum State { IDLE, WORK, BREAK }
    private State state = State.IDLE;

    private long remainingTime = 0;
    private long endTime = 0;
    private boolean isRunning = false;

    private static final long WORK_TIME = 25 * 60 * 1000;   // 25 minutes
    private static final long BREAK_TIME = 5 * 60 * 1000;   // 5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Pomodoro Timer");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        statusView = new TextView(this);
        statusView.setTextSize(18);
        statusView.setPadding(0, 0, 0, 8);
        root.addView(statusView);

        timerView = new TextView(this);
        timerView.setTextSize(48);
        timerView.setPadding(0, 20, 0, 20);
        root.addView(timerView);

        startButton = new Button(this);
        startButton.setText("Start Work");
        startButton.setTextSize(18);
        startButton.setOnClickListener(v -> startPomodoro());
        root.addView(startButton);

        resetButton = new Button(this);
        resetButton.setText("Reset");
        resetButton.setTextSize(18);
        resetButton.setOnClickListener(v -> resetTimer());
        root.addView(resetButton);

        handler = new Handler();

        setContentView(root);
    }

    private void startPomodoro() {
        if (isRunning) return;
        state = State.WORK;
        remainingTime = WORK_TIME;
        endTime = System.currentTimeMillis() + remainingTime;
        isRunning = true;
        startButton.setText("Pause");
        handler.post(updateTimer);
    }

    private void resetTimer() {
        handler.removeCallbacks(updateTimer);
        isRunning = false;
        state = State.IDLE;
        remainingTime = 0;
        endTime = 0;
        timerView.setText("25:00");
        statusView.setText("Ready");
        startButton.setText("Start Work");
    }

    private final Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            long remaining = endTime - now;
            if (remaining <= 0) {
                if (state == State.WORK) {
                    statusView.setText("Work time complete! Take a break.");
                    state = State.BREAK;
                    remainingTime = BREAK_TIME;
                    endTime = System.currentTimeMillis() + remainingTime;
                    handler.postDelayed(this, 1000);
                    return;
                } else {
                    isRunning = false;
                    statusView.setText("Break complete! Ready for work.");
                    timerView.setText("25:00");
                    startButton.setText("Start Work");
                    return;
                }
            }
            remainingTime = remaining;
            int seconds = (int) (remaining / 1000);
            timerView.setText(String.format(Locale.getDefault(), "%02d:%02d",
                seconds / 60, seconds % 60));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimer);
    }
}
