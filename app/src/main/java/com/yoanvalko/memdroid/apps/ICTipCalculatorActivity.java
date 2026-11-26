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
 * Simple tip calculator.
 */
public class ICTipCalculatorActivity extends AppCompatActivity {

    private EditText billAmountView;
    private EditText tipPercentView;
    private TextView tipAmountView;
    private TextView totalView;
    private Button calculateButton;

    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Tip Calculator");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        billAmountView = new EditText(this);
        billAmountView.setHint("Bill amount ($)");
        billAmountView.setHintTextColor(0xFF999999);
        billAmountView.setTextSize(18);
        billAmountView.setPadding(8, 8, 8, 8);
        root.addView(billAmountView);

        tipPercentView = new EditText(this);
        tipPercentView.setHint("Tip percent (%)");
        tipPercentView.setHintTextColor(0xFF999999);
        tipPercentView.setTextSize(18);
        tipPercentView.setPadding(8, 8, 8, 8);
        root.addView(tipPercentView);

        tipAmountView = new TextView(this);
        tipAmountView.setTextSize(16);
        tipAmountView.setPadding(0, 8, 0, 4);
        root.addView(tipAmountView);

        totalView = new TextView(this);
        totalView.setTextSize(16);
        totalView.setPadding(0, 0, 0, 20);
        root.addView(totalView);

        calculateButton = new Button(this);
        calculateButton.setText("Calculate");
        calculateButton.setTextSize(18);
        calculateButton.setOnClickListener(v -> calculateTip());
        root.addView(calculateButton);

        setContentView(root);
    }

    private void calculateTip() {
        String billStr = billAmountView.getText().toString().trim();
        String percentStr = tipPercentView.getText().toString().trim();

        if (TextUtils.isEmpty(billStr)) {
            tipAmountView.setText("Tip amount: $0.00");
            totalView.setText("Total: $0.00");
            return;
        }

        try {
            double bill = Double.parseDouble(billStr);
            double percent = TextUtils.isEmpty(percentStr) ? 15.0 : Double.parseDouble(percentStr);
            double tip = bill * (percent / 100.0);
            double total = bill + tip;

            tipAmountView.setText("Tip amount: " + currencyFormat.format(tip));
            totalView.setText("Total: " + currencyFormat.format(total));
        } catch (NumberFormatException e) {
            tipAmountView.setText("Tip amount: $0.00");
            totalView.setText("Total: $0.00");
        }
    }
}
