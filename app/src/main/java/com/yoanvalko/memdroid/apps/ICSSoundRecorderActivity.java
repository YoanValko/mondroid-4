package com.yoanvalko.memdroid.apps;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Simple voice recorder using MediaRecorder.
 */
public class ICSSoundRecorderActivity extends AppCompatActivity {

    private MediaRecorder recorder;
    private boolean isRecording = false;
    private TextView statusView;
    private Button recordButton;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Sound Recorder");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        statusView = new TextView(this);
        statusView.setText("Ready to record");
        statusView.setTextSize(14);
        statusView.setPadding(0, 0, 0, 16);
        root.addView(statusView);

        recordButton = new Button(this);
        recordButton.setText("Start Recording");
        recordButton.setTextSize(18);
        recordButton.setOnClickListener(v -> toggleRecording());
        root.addView(recordButton);

        setContentView(root);
    }

    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (dir == null) dir = getFilesDir();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            outputFile = new File(dir, "recording_" + sdf.format(new Date()) + ".m4a").getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(outputFile);
            recorder.prepare();
            recorder.start();

            isRecording = true;
            recordButton.setText("Stop Recording");
            statusView.setText("Recording...");
            statusView.setTextColor(0xFFFF0000);
        } catch (IOException e) {
            Toast.makeText(this, "Recording failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        isRecording = false;
        recordButton.setText("Start Recording");
        statusView.setText("Saved: " + outputFile);
        statusView.setTextColor(0xFF000000);
        Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
