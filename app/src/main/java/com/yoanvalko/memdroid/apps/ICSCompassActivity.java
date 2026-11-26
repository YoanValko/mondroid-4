package com.yoanvalko.memdroid.apps;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

/**
 * Simple compass using the device's magnetic field sensor.
 */
public class ICSCompassActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magneticFieldSensor;
    private Sensor accelerometerSensor;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private TextView headingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Compass");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        headingView = new TextView(this);
        headingView.setText("0°");
        headingView.setTextSize(64);
        headingView.setPadding(0, 20, 0, 20);
        root.addView(headingView);

        TextView dirView = new TextView(this);
        dirView.setText("North");
        dirView.setTextSize(24);
        dirView.setPadding(0, 0, 0, 32);
        root.addView(dirView);

        setContentView(root);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magneticFieldSensor != null && accelerometerSensor != null) {
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            headingView.setText("N/A");
            headingView.setTextSize(32);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
        }
        if (gravity != null && geomagnetic != null) {
            float[] rotationMatrix = new float[9];
            float[] I = new float[9];
            if (SensorManager.getRotationMatrix(rotationMatrix, I, gravity, geomagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuth = orientation[0];
                float heading = (float) Math.toDegrees(azimuth);
                heading = (heading + 360) % 360;
                headingView.setText(String.format("%.0f°", heading));
                String dir = getDirection(heading);
                TextView dirView = (TextView) findViewById(android.R.id.text1);
                // We don't have a separate dir view in this layout, just update heading
            }
        }
    }

    private String getDirection(float heading) {
        if (heading >= 337.5 || heading < 22.5) return "N";
        if (heading >= 22.5 && heading < 67.5) return "NE";
        if (heading >= 67.5 && heading < 112.5) return "E";
        if (heading >= 112.5 && heading < 157.5) return "SE";
        if (heading >= 157.5 && heading < 202.5) return "S";
        if (heading >= 202.5 && heading < 247.5) return "SW";
        if (heading >= 247.5 && heading < 292.5) return "W";
        if (heading >= 292.5 && heading < 337.5) return "NW";
        return "N";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
