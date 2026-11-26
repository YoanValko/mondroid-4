package com.example.icslauncher.apps;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

/**
 * Simple flashlight using camera flash.
 */
public class ICSFlashlightActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
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

        TextView info = new TextView(this);
        info.setText("Use your camera flash as a flashlight");
        info.setTextSize(14);
        info.setPadding(0, 16, 0, 0);
        root.addView(info);

        setContentView(root);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 0) {
                cameraId = cameraIds[0];
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Boolean flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvailable != null && flashAvailable) {
                    toggleButton.setEnabled(true);
                } else {
                    toggleButton.setEnabled(false);
                    toggleButton.setText("No flash available");
                }
            } else {
                toggleButton.setEnabled(false);
                toggleButton.setText("No camera available");
            }
        } catch (Exception e) {
            toggleButton.setEnabled(false);
            toggleButton.setText("Camera error");
        }
    }

    private void toggleFlashlight() {
        if (cameraManager == null || cameraId == null) return;

        try {
            if (isOn) {
                cameraManager.setTorchMode(cameraId, false);
                isOn = false;
                toggleButton.setText("Turn On");
            } else {
                cameraManager.setTorchMode(cameraId, true);
                isOn = true;
                toggleButton.setText("Turn Off");
            }
        } catch (Exception e) {
            toggleButton.setEnabled(false);
            toggleButton.setText("Error");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraManager != null && cameraId != null && isOn) {
            try {
                cameraManager.setTorchMode(cameraId, false);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
