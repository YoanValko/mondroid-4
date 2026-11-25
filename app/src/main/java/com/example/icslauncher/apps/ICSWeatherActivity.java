package com.example.icslauncher.apps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.Random;

/**
 * Mock weather app showing current weather conditions.
 */
public class ICSWeatherActivity extends AppCompatActivity {

    private TextView locationView;
    private TextView tempView;
    private TextView conditionView;
    private TextView humidityView;
    private TextView windView;
    private Button refreshButton;

    private final String[] conditions = {"Sunny", "Partly Cloudy", "Cloudy", "Overcast", "Light Rain", "Rain", "Thunderstorms", "Snow"};
    private final String[] cities = {"New York", "London", "Tokyo", "Paris", "Berlin", "Sydney", "Moscow", "Cairo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Weather");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        locationView = new TextView(this);
        locationView.setTextSize(18);
        locationView.setPadding(0, 0, 0, 8);
        root.addView(locationView);

        tempView = new TextView(this);
        tempView.setTextSize(48);
        tempView.setPadding(0, 10, 0, 10);
        root.addView(tempView);

        conditionView = new TextView(this);
        conditionView.setTextSize(16);
        conditionView.setPadding(0, 0, 0, 16);
        root.addView(conditionView);

        humidityView = new TextView(this);
        humidityView.setTextSize(14);
        humidityView.setPadding(0, 0, 0, 4);
        root.addView(humidityView);

        windView = new TextView(this);
        windView.setTextSize(14);
        windView.setPadding(0, 0, 0, 20);
        root.addView(windView);

        refreshButton = new Button(this);
        refreshButton.setText("Refresh");
        refreshButton.setTextSize(16);
        refreshButton.setOnClickListener(v -> updateWeather());
        root.addView(refreshButton);

        setContentView(root);

        updateWeather();
    }

    private void updateWeather() {
        Random random = new Random();
        String city = cities[random.nextInt(cities.length)];
        String condition = conditions[random.nextInt(conditions.length)];
        int temp = random.nextInt(40) - 10; // -10 to 30°C
        int humidity = random.nextInt(60) + 20; // 20% to 80%
        int windSpeed = random.nextInt(30); // 0 to 30 km/h

        locationView.setText(city);
        tempView.setText(String.format("%d°C", temp));
        conditionView.setText(condition);
        humidityView.setText("Humidity: " + humidity + "%");
        windView.setText("Wind: " + windSpeed + " km/h");
    }
}
