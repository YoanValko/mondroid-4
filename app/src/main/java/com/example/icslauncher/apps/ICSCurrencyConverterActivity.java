package com.example.icslauncher.apps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.text.DecimalFormat;

/**
 * Simple currency converter between USD and EUR.
 */
public class ICSCurrencyConverterActivity extends AppCompatActivity {

    private EditText usdView;
    private EditText eurView;
    private TextView rateView;
    private Button convertToEurButton;
    private Button convertToUsdButton;

    private static final double USD_TO_EUR = 0.92;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Currency Converter");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        rateView = new TextView(this);
        rateView.setText("1 USD = 0.92 EUR");
        rateView.setTextSize(14);
        rateView.setPadding(0, 0, 0, 16);
        root.addView(rateView);

        usdView = new EditText(this);
        usdView.setHint("USD");
        usdView.setHintTextColor(0xFF999999);
        usdView.setTextSize(18);
        usdView.setPadding(8, 8, 8, 8);
        root.addView(usdView);

        eurView = new EditText(this);
        eurView.setHint("EUR");
        eurView.setHintTextColor(0xFF999999);
        eurView.setTextSize(18);
        eurView.setPadding(8, 8, 8, 8);
        root.addView(eurView);

        convertToEurButton = new Button(this);
        convertToEurButton.setText("Convert to EUR");
        convertToEurButton.setTextSize(16);
        convertToEurButton.setOnClickListener(v -> convertToEUR());
        root.addView(convertToEurButton);

        convertToUsdButton = new Button(this);
        convertToUsdButton.setText("Convert to USD");
        convertToUsdButton.setTextSize(16);
        convertToUsdButton.setOnClickListener(v -> convertToUSD());
        root.addView(convertToUsdButton);

        setContentView(root);
    }

    private void convertToEUR() {
        String usdStr = usdView.getText().toString().trim();
        if (TextUtils.isEmpty(usdStr)) {
            eurView.setText("");
            return;
        }
        try {
            double usd = Double.parseDouble(usdStr);
            double eur = usd * USD_TO_EUR;
            eurView.setText(new DecimalFormat("#,##0.00").format(eur));
        } catch (NumberFormatException e) {
            eurView.setText("");
        }
    }

    private void convertToUSD() {
        String eurStr = eurView.getText().toString().trim();
        if (TextUtils.isEmpty(eurStr)) {
            usdView.setText("");
            return;
        }
        try {
            double eur = Double.parseDouble(eurStr);
            double usd = eur / USD_TO_EUR;
            usdView.setText(new DecimalFormat("#,##0.00").format(usd));
        } catch (NumberFormatException e) {
            usdView.setText("");
        }
    }
}
