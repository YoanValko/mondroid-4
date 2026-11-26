package com.yoanvalko.memdroid.apps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.text.DecimalFormat;

/**
 * Simple BMI (Body Mass Index) calculator.
 */
public class ICSBMICalculatorActivity extends AppCompatActivity {

    private EditText weightView;
    private EditText heightView;
    private TextView bmiView;
    private TextView categoryView;
    private Button calculateButton;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("BMI Calculator");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        weightView = new EditText(this);
        weightView.setHint("Weight (kg)");
        weightView.setHintTextColor(0xFF999999);
        weightView.setTextSize(18);
        weightView.setPadding(8, 8, 8, 8);
        root.addView(weightView);

        heightView = new EditText(this);
        heightView.setHint("Height (m)");
        heightView.setHintTextColor(0xFF999999);
        heightView.setTextSize(18);
        heightView.setPadding(8, 8, 8, 8);
        root.addView(heightView);

        bmiView = new TextView(this);
        bmiView.setTextSize(24);
        bmiView.setPadding(0, 16, 0, 8);
        root.addView(bmiView);

        categoryView = new TextView(this);
        categoryView.setTextSize(16);
        categoryView.setPadding(0, 0, 0, 20);
        root.addView(categoryView);

        calculateButton = new Button(this);
        calculateButton.setText("Calculate BMI");
        calculateButton.setTextSize(18);
        calculateButton.setOnClickListener(v -> calculateBMI());
        root.addView(calculateButton);

        setContentView(root);
    }

    private void calculateBMI() {
        String weightStr = weightView.getText().toString().trim();
        String heightStr = heightView.getText().toString().trim();

        if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
            bmiView.setText("BMI: --");
            categoryView.setText("Enter both values");
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            if (weight <= 0 || height <= 0) {
                bmiView.setText("BMI: --");
                categoryView.setText("Enter positive values");
                return;
            }

            double bmi = weight / (height * height);
            bmiView.setText("BMI: " + decimalFormat.format(bmi));
            categoryView.setText(getBMICategory(bmi));
        } catch (NumberFormatException e) {
            bmiView.setText("BMI: --");
            categoryView.setText("Enter valid numbers");
        }
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal weight";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }
}
