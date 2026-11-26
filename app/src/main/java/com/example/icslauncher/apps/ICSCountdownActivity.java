package com.example.icslauncher.apps;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.Locale;

/**
 * Countdown timer - counts down from a specified time.
 */
public class ICSCountdownActivity extends AppCompatActivity {

    private EditText minutesInput;
    private EditText secondsInput;
    private TextView timerView;
    private Button startButton;
    private Button resetButton;
    private Handler handler;
    private long remainingTime = 0;
    private long endTime = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Countdown Timer");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);

        minutesInput = new EditText(this);
        minutesInput.setHint("Min");
        minutesInput.setHintTextColor(0xFF999999);
        minutesInput.setTextSize(18);
        minutesInput.setPadding(8, 8, 8, 8);
        minutesInput.setText("0");
        LinearLayout.LayoutParams minParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        inputRow.addView(minutesInput, minParams);

        TextView minLabel = new TextView(this);
        minLabel.setText("min");
        minLabel.setTextSize(18);
        minLabel.setPadding(4, 8, 4, 8);
        inputRow.addView(minLabel);

        secondsInput = new EditText(this);
        secondsInput.setHint("Sec");
        secondsInput.setHintTextColor(0xFF999999);
        secondsInput.setTextSize(18);
        secondsInput.setPadding(8, 8, 8, 8);
        secondsInput.setText("0");
        LinearLayout.LayoutParams secParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        inputRow.addView(secondsInput, secParams);

        TextView secLabel = new TextView(this);
        secLabel.setText("sec");
        secLabel.setTextSize(18);
        secLabel.setPadding(4, 8, 4, 8);
        inputRow.addView(secLabel);

        root.addView(inputRow);

        timerView = new TextView(this);
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

        updateDisplay();
    }

    private void toggleTimer() {
        if (isRunning) {
            handler.removeCallbacks(updateTimer);
            isRunning = false;
            startButton.setText("Start");
        } else {
            String minStr = minutesInput.getText().toString().trim();
            String secStr = secondsInput.getText().toString().trim();
            int minutes = TextUtils.isEmpty(minStr) ? 0 : Integer.parseInt(minStr);
            int seconds = TextUtils.isEmpty(secStr) ? 0 : Integer.parseInt(secStr);
            remainingTime = (minutes * 60 + seconds) * 1000;
            if (remainingTime <= 0) {
                return;
            }
            endTime = System.currentTimeMillis() + remainingTime;
            handler.post(updateTimer);
            isRunning = true;
            startButton.setText("Pause");
        }
    }

    private void resetTimer() {
        handler.removeCallbacks(updateTimer);
        isRunning = false;
        remainingTime = 0;
        endTime = 0;
        timerView.setText("00:00");
        startButton.setText("Start");
        minutesInput.setText("0");
        secondsInput.setText("0");
    }

    private final Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            long remaining = endTime - now;
            if (remaining <= 0) {
                remainingTime = 0;
                timerView.setText("00:00");
                isRunning = false;
                startButton.setText("Start");
                return;
            }
            remainingTime = remaining;
            int seconds = (int) (remaining / 1000);
            timerView.setText(String.format(Locale.getDefault(), "%02d:%02d",
                seconds / 60, seconds % 60));
            handler.postDelayed(this, 1000);
        }
    };

    private void updateDisplay() {
        int seconds = (int) (remainingTime / 1000);
        timerView.setText(String.format(Locale.getDefault(), "%02d:%02d",
            seconds / 60, seconds % 60));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimer);
    }
}
