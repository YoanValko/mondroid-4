package com.example.icslauncher.apps;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.io.IOException;

/**
 * Simple flashlight using the camera flash.
 */
public class ICSFlashlightActivity extends AppCompatActivity {

    private Camera camera;
    private Camera.Parameters params;
    private boolean isOn = false;
    private Button toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Flashlight");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        toggleButton = new Button(this);
        toggleButton.setText("Turn On");
        toggleButton.setTextSize(18);
        toggleButton.setOnClickListener(v -> toggleFlashlight());
        root.addView(toggleButton);

        setContentView(root);

        try {
            camera = Camera.open();
            params = camera.getParameters();
        } catch (Exception e) {
            toggleButton.setEnabled(false);
            toggleButton.setText("Flashlight not available");
        }
    }

    private void toggleFlashlight() {
        if (camera == null || params == null) return;

        if (isOn) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isOn = false;
            toggleButton.setText("Turn On");
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isOn = true;
            toggleButton.setText("Turn Off");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
