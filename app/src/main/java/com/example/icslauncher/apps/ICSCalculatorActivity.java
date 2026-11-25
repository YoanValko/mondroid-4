package com.example.icslauncher.apps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.icslauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Reimplementation of the Ice Cream Sandwich Calculator. Basic arithmetic (+ - * /) with a
 * simple expression evaluator; no network, no proprietary code.
 */
public class ICSCalculatorActivity extends AppCompatActivity {

    private EditText display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        display = new EditText(this);
        display.setTextSize(32);
        display.setFocusable(false);
        display.setClickable(false);
        display.setPadding(0, 8, 0, 8);
        root.addView(display);

        TextView expr = new TextView(this);
        expr.setTextSize(18);
        expr.setText("0");
        root.addView(expr);

        String[][] rows = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "C", "+"},
                {"=",}
        };
        for (String[] row : rows) {
            LinearLayout r = new LinearLayout(this);
            r.setOrientation(LinearLayout.HORIZONTAL);
            for (final String cap : row) {
                Button b = new Button(this);
                b.setText(cap);
                b.setTextSize(20);
                b.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                b.setOnClickListener(v -> onKey(cap));
                r.addView(b);
            }
            root.addView(r);
        }

        setContentView(root);
    }

    private void onKey(String cap) {
        String cur = display.getText().toString();
        if (cap.equals("C")) {
            display.setText("");
            return;
        }
        if (cap.equals("=")) {
            String result = evaluate(cur);
            display.setText(result);
            return;
        }
        display.setText(cur + cap);
    }

    /** Tiny expression evaluator supporting + - * / and decimals. */
    private String evaluate(String expr) {
        expr = expr.replaceAll(" ", "");
        if (expr.isEmpty()) return "0";
        List<String> tokens = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (char ch : expr.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                num.append(ch);
            } else {
                if (num.length() > 0) {
                    tokens.add(num.toString());
                    num.setLength(0);
                }
                tokens.add(String.valueOf(ch));
            }
        }
        if (num.length() > 0) tokens.add(num.toString());
        try {
            // Pass 1: * and /
            for (int i = 1; i < tokens.size(); i += 2) {
                String op = tokens.get(i);
                if (op.equals("*") || op.equals("/")) {
                    double a = Double.parseDouble(tokens.get(i - 1));
                    double b = Double.parseDouble(tokens.get(i + 1));
                    double r = op.equals("*") ? a * b : (b == 0 ? Double.NaN : a / b);
                    tokens.set(i - 1, fmt(r));
                    tokens.remove(i);
                    tokens.remove(i);
                    i -= 2;
                }
            }
            // Pass 2: + and -
            double result = Double.parseDouble(tokens.get(0));
            for (int i = 1; i < tokens.size(); i += 2) {
                double b = Double.parseDouble(tokens.get(i + 1));
                if (tokens.get(i).equals("+")) result += b; else result -= b;
            }
            return fmt(result);
        } catch (Exception e) {
            return "Error";
        }
    }

    private String fmt(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "Error";
        if (v == Math.floor(v)) return String.valueOf((long) v);
        return String.valueOf(v);
    }
}
