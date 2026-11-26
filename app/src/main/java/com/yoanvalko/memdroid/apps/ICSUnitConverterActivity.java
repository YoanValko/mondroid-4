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
 * Simple unit converter for length, weight, and temperature.
 */
public class ICSUnitConverterActivity extends AppCompatActivity {

    private EditText inputView;
    private TextView resultView;
    private Button convertButton;
    private Button swapButton;

    private String currentCategory = "length";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Unit Converter");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        inputView = new EditText(this);
        inputView.setHint("Value to convert");
        inputView.setHintTextColor(0xFF999999);
        inputView.setTextSize(18);
        inputView.setPadding(8, 8, 8, 8);
        root.addView(inputView);

        resultView = new TextView(this);
        resultView.setTextSize(18);
        resultView.setPadding(0, 12, 0, 12);
        root.addView(resultView);

        convertButton = new Button(this);
        convertButton.setText("Convert");
        convertButton.setTextSize(16);
        convertButton.setOnClickListener(v -> performConversion());
        root.addView(convertButton);

        Button swapButton = new Button(this);
        swapButton.setText("Swap Units");
        swapButton.setTextSize(16);
        swapButton.setOnClickListener(v -> swapUnits());
        root.addView(swapButton);

        setContentView(root);
    }

    private void performConversion() {
        String inputStr = inputView.getText().toString().trim();
        if (TextUtils.isEmpty(inputStr)) {
            resultView.setText("Result: --");
            return;
        }

        try {
            double input = Double.parseDouble(inputStr);
            double result = convert(input, currentCategory);
            resultView.setText("Result: " + new DecimalFormat("#,##0.00").format(result));
        } catch (NumberFormatException e) {
            resultView.setText("Result: --");
        }
    }

    private double convert(double value, String category) {
        if ("length".equals(category)) {
            // Convert meters to feet
            return value * 3.28084;
        } else if ("weight".equals(category)) {
            // Convert kilograms to pounds
            return value * 2.20462;
        } else if ("temperature".equals(category)) {
            // Convert Celsius to Fahrenheit
            return value * 9.0 / 5.0 + 32.0;
        }
        return value;
    }

    private void swapUnits() {
        if ("length".equals(currentCategory)) {
            currentCategory = "weight";
        } else if ("weight".equals(currentCategory)) {
            currentCategory = "temperature";
        } else {
            currentCategory = "length";
        }
    }
}
