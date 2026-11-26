package com.yoanvalko.memdroid.apps;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Age calculator based on birth date.
 */
public class ICSVAGECalculatorActivity extends AppCompatActivity {

    private DatePicker birthDatePicker;
    private TextView ageView;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Age Calculator");
        title.setTextSize(20);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        root.addView(title);

        birthDatePicker = new DatePicker(this);
        root.addView(birthDatePicker);

        ageView = new TextView(this);
        ageView.setTextSize(24);
        ageView.setPadding(0, 16, 0, 8);
        root.addView(ageView);

        calculateButton = new Button(this);
        calculateButton.setText("Calculate Age");
        calculateButton.setTextSize(18);
        calculateButton.setOnClickListener(v -> calculateAge());
        root.addView(calculateButton);

        setContentView(root);

        // Calculate initial age
        calculateAge();
    }

    private void calculateAge() {
        Calendar birth = Calendar.getInstance();
        birth.set(birthDatePicker.getYear(), birthDatePicker.getMonth(), birthDatePicker.getDayOfMonth());

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
            (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
             today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        ageView.setText("Age: " + age + " years");
    }
}
